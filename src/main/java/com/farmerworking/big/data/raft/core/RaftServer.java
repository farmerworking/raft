package com.farmerworking.big.data.raft.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.farmerworking.big.data.raft.core.communication.Sender;
import com.farmerworking.big.data.raft.core.events.RaftEvent;
import com.farmerworking.big.data.raft.core.events.timeout.ElectionTimeoutEvent;
import com.farmerworking.big.data.raft.core.events.rpc.HeartBeatEvent;
import com.farmerworking.big.data.raft.core.events.rpc.HeartBeatReplyEvent;
import com.farmerworking.big.data.raft.core.events.rpc.HeartBeatRequestTimeoutEvent;
import com.farmerworking.big.data.raft.core.events.timeout.HeartBeatTimeoutEvent;
import com.farmerworking.big.data.raft.core.events.rpc.RequestVoteEvent;
import com.farmerworking.big.data.raft.core.events.rpc.RequestVoteReplyEvent;
import com.farmerworking.big.data.raft.core.events.rpc.RequestVoteTimeoutEvent;
import com.farmerworking.big.data.raft.core.timeout.managers.ElectionTimeoutManager;
import com.farmerworking.big.data.raft.core.timeout.managers.HeartBeatTimeoutManager;
import com.farmerworking.big.data.raft.core.timeout.managers.RpcTimeoutManager;
import com.farmerworking.big.data.raft.core.timeout.tasks.ElectionTimeoutTask;
import com.farmerworking.big.data.raft.core.timeout.tasks.HeartBeatTimeoutTask;
import com.farmerworking.big.data.raft.core.timeout.tasks.RpcTimeoutTask;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaftServer {
    private static final int VOTED_FOR_INIT_STATE = -1;
    private static Logger LOGGER = LoggerFactory.getLogger(RaftEvent.class);

    /**
     * immutable config
     */
    private final Set<ServerMetaData> serverSet;
    private final int serverId;

    /**
     * communication layer
     */
    private Sender sender;

    /**
     * other
     */
    private EventBus eventBus;

    /**
     * general state
     */
    private RaftServerRole role;
    private AtomicLong currentTerm;
    private RpcTimeoutManager rpcTimeoutManager;

    /**
     * follower state
     */
    private ElectionTimeoutManager electionElectionTimeoutManager;
    private int votedFor;


    /**
     * candidate state
     */
    private Map<Integer, Boolean> voteResult;

    /**
     * leader state
     */
    private HeartBeatTimeoutManager heartBeatTimeoutManager;

    public RaftServer(int serverId,
                      Set<ServerMetaData> serverSet,
                      Sender sender,
                      long heartBeatTimeout,
                      long electionTimeout,
                      long rpcTimeout,
                      int randomTimeoutRange) {
        this.serverId = serverId;
        this.serverSet = serverSet;
        this.sender = sender;

        this.voteResult = Maps.newHashMap();
        this.eventBus = new EventBus();

        electionElectionTimeoutManager = new ElectionTimeoutManager(eventBus, electionTimeout, randomTimeoutRange);
        heartBeatTimeoutManager = new HeartBeatTimeoutManager(eventBus, heartBeatTimeout);
        rpcTimeoutManager = new RpcTimeoutManager(eventBus, rpcTimeout);
    }

    public void initialize() {
        role = RaftServerRole.FOLLOWER;
        currentTerm = new AtomicLong(0);
        votedFor = VOTED_FOR_INIT_STATE;
        eventBus.register(this);
    }

    public void start() {
        electionElectionTimeoutManager.start();
    }

    @Subscribe
    private void handleElectionTimeoutEvent(ElectionTimeoutEvent event) {
        turnCandidate();

        for(ServerMetaData server : serverSet) {
            requestVote(server);
        }
    }

    @Subscribe
    private void handleRequestVoteEvent(RequestVoteEvent event) {
        long term = event.getTerm();

        if (term < currentTerm.get()) {
            sender.requestVoteReply(event.getServer(), event.getTraceId(), currentTerm.get(), false);
        } else if (term > currentTerm.get()) {
            turnFollower(term);
            handleRequestVoteEvent(event);
        } else {
            if (role == RaftServerRole.LEADER) {
                sender.requestVoteReply(event.getServer(), event.getTraceId(), currentTerm.get(), false);
            } else if (votedFor == event.getServer().getServerId() || votedFor == VOTED_FOR_INIT_STATE) {
                 // rpc timeout and resend request vote request || has not vote yet
                votedFor = event.getServer().getServerId();
                sender.requestVoteReply(event.getServer(), event.getTraceId(), currentTerm.get(), true);
            } else {
                sender.requestVoteReply(event.getServer(), event.getTraceId(), currentTerm.get(), false);
            }
        }
    }

    /**
     * only possible cases:
     * 1. term > currentTerm
     * 2. term == currentTerm
     * @param event
     */
    @Subscribe
    private void handleRequestVoteReplyEvent(RequestVoteReplyEvent event) {
        rpcTimeoutManager.stop(event.getTraceId());

        if (event.getTerm() > currentTerm.get()) {
            turnFollower(event.getTerm());
        } else {
            if (role == RaftServerRole.CANDIDATE) {
                voteResult.put(event.getServer().getServerId(), event.isVoteGranted());
                if (hasMajorityVotes()) {
                    turnLeader();
                }
            } else {
                // do nothing
            }
        }
    }

    /**
     * only possible cases:
     * 1. event.getTerm() == currentTerm.get()
     * 2. event.getTerm() < currentTerm.get()
     * @param event
     */
    @Subscribe
    private void handleRequestVoteTimeoutEvent(RequestVoteTimeoutEvent event) {
        if (event.getTerm() == currentTerm.get()) {
            if (role == RaftServerRole.CANDIDATE) {
                requestVote(event.getServer());
            } else {
                // do nothing
            }
        } else {
            // do nothing
        }
    }

    @Subscribe
    private void handleHeartBeatTimeoutEvent(HeartBeatTimeoutEvent event) {
        for(ServerMetaData server : serverSet) {
            if (server.getServerId() == serverId) {
                continue;
            }

            heartBeat(server);
        }
    }

    @Subscribe
    private void handleHeartBeatEvent(HeartBeatEvent event) {
        if (event.getTerm() > currentTerm.get()) {
            turnFollower(event.getTerm());
            handleHeartBeatEvent(event);
        } else if (event.getTerm() < currentTerm.get()){
            // turn sender to follower
            sender.heartBeatReply(event.getServer(), event.getTraceId(), currentTerm.get());
        } else if (role == RaftServerRole.FOLLOWER) {
            electionElectionTimeoutManager.start();
            sender.heartBeatReply(event.getServer(), event.getTraceId(), currentTerm.get());
        } else { // candidate and term == currentTerm
            /**
             * 1. two node A and B get isolated
             * 2. election time out, A and B both turn to a candidate with term + 1(newTerm)
             * 3. A's network recover and become the leader of the term(newTerm)
             * 4. B's network recover, hear heart beat request
             * 5. to reduce leader exchange, B become a follower and response to heart beat request
             */
            turnFollower(event.getTerm());
            handleHeartBeatEvent(event);
        }
    }

    /**
     * possible cases:
     * 1. term > currentTerm
     * 2. term == currentTerm
     * @param event
     */
    @Subscribe
    private void handleHeartBeatReplyEvent(HeartBeatReplyEvent event) {
        rpcTimeoutManager.stop(event.getTraceId());

        if (event.getTerm() > currentTerm.get()) {
            turnFollower(event.getTerm());
        } else {
            // do nothing
        }
    }

    @Subscribe
    private void handleHeartBeatRequestTimeoutEvent(HeartBeatRequestTimeoutEvent event) {
        if (event.getTerm() == currentTerm.get()) {
            if (role == RaftServerRole.LEADER) {
                heartBeat(event.getServer());
            } else {
                // impossible case. one term one leader at most
                throw new RuntimeException("in the same term, leader exchanged");
            }
        } else {
            // do nothing
        }
    }

    @Subscribe
    private void eventTracer(RaftEvent event) {
        LOGGER.debug(event.toString());
    }

    private boolean hasMajorityVotes() {
        return voteResult.values().stream().filter(vote -> vote).count() >= Math.ceil(serverSet.size());
    }

    private void turnCandidate() {
        stateClear();

        role = RaftServerRole.CANDIDATE;
        currentTerm.incrementAndGet();
        electionElectionTimeoutManager.start();
    }

    private void turnLeader() {
        stateClear();

        role = RaftServerRole.LEADER;
        heartBeatTimeoutManager.start();
    }

    private void turnFollower(long term) {
        stateClear();

        role = RaftServerRole.FOLLOWER;
        currentTerm.set(term);
        electionElectionTimeoutManager.start();
    }

    private void stateClear() {
        // general state clear
        rpcTimeoutManager.clear();

        // follower state clear
        electionElectionTimeoutManager.stop();
        votedFor = VOTED_FOR_INIT_STATE;

        // candidate state clear
        voteResult.clear();

        // leader state clear
        heartBeatTimeoutManager.stop();
    }

    private void requestVote(ServerMetaData server) {
        RequestVoteTimeoutEvent event = new RequestVoteTimeoutEvent(currentTerm.get(), server);
        rpcTimeoutManager.start(event);

        sender.requestVote(server, event.getTraceId(), currentTerm.get());
    }

    private void heartBeat(ServerMetaData server) {
        HeartBeatRequestTimeoutEvent event = new HeartBeatRequestTimeoutEvent(currentTerm.get(), server);
        rpcTimeoutManager.start(event);

        sender.heartBeat(server, event.getTraceId(), currentTerm.get());
    }
}
