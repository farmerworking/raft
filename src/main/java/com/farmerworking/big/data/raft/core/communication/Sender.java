package com.farmerworking.big.data.raft.core.communication;

import com.farmerworking.big.data.raft.core.ServerMetaData;

/**
 * Created by John on 18/4/13.
 */
public interface Sender {
    void requestVote(ServerMetaData server, String traceId, long term);

    void requestVoteReply(ServerMetaData server, String traceId, long currentTerm, boolean isVoteGranted);

    void heartBeat(ServerMetaData server, String traceId, long term);

    void heartBeatReply(ServerMetaData server, String traceId, long term);
}
