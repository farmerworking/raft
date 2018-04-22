package com.farmerworking.big.data.raft.core.communication;

import java.util.Map;

import com.farmerworking.big.data.raft.core.ServerMetaData;
import com.farmerworking.big.data.raft.core.events.rpc.HeartBeatEvent;
import com.farmerworking.big.data.raft.core.events.rpc.HeartBeatReplyEvent;
import com.farmerworking.big.data.raft.core.events.rpc.RequestVoteEvent;
import com.farmerworking.big.data.raft.core.events.rpc.RequestVoteReplyEvent;

public class SimpleSenderImpl implements Sender{
    private final Map<Integer, ServerMetaData> map;
    private final ServerMetaData owner;

    public SimpleSenderImpl(Map<Integer, ServerMetaData> map, ServerMetaData owner) {
        this.map = map;
        this.owner = owner;
    }

    @Override
    public void requestVote(ServerMetaData target, RaftRpcBaseData data) {
        map.get(target.getServerId()).getEventBus().post(new RequestVoteEvent(data, owner));
    }

    @Override
    public void requestVoteReply(ServerMetaData target, VoteReplyData data) {
        map.get(target.getServerId()).getEventBus().post(new RequestVoteReplyEvent(data, owner));
    }

    @Override
    public void heartBeat(ServerMetaData target, RaftRpcBaseData data) {
        map.get(target.getServerId()).getEventBus().post(new HeartBeatEvent(data, owner));
    }

    @Override
    public void heartBeatReply(ServerMetaData target, RaftRpcBaseData data) {
        map.get(target.getServerId()).getEventBus().post(new HeartBeatReplyEvent(data, owner));
    }
}
