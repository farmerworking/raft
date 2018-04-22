package com.farmerworking.big.data.raft.core.communication;

import com.farmerworking.big.data.raft.core.ServerMetaData;

/**
 * Created by John on 18/4/13.
 */
public interface Sender {
    void requestVote(ServerMetaData target, RaftRpcBaseData data);

    void requestVoteReply(ServerMetaData target, VoteReplyData data);

    void heartBeat(ServerMetaData target, RaftRpcBaseData data);

    void heartBeatReply(ServerMetaData target, RaftRpcBaseData data);
}
