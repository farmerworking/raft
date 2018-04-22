package com.farmerworking.big.data.raft.core.communication;

import lombok.Data;

@Data
public class RaftRpcBaseData {
    private String traceId;
    private long term;

    public RaftRpcBaseData(String traceId, long term) {
        this.traceId = traceId;
        this.term = term;
    }
}
