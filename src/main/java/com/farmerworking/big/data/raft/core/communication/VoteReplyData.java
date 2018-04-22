package com.farmerworking.big.data.raft.core.communication;

import lombok.Data;

@Data
public class VoteReplyData extends RaftRpcBaseData {
    private boolean isVoteGranted;

    public VoteReplyData(String traceId, long term, boolean isVoteGranted) {
        super(traceId, term);
        this.isVoteGranted = isVoteGranted;
    }
}
