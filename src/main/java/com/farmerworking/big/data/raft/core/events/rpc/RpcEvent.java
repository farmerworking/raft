package com.farmerworking.big.data.raft.core.events.rpc;

import java.util.UUID;

import com.farmerworking.big.data.raft.core.ServerMetaData;
import com.farmerworking.big.data.raft.core.events.RaftEvent;
import lombok.Data;

@Data
public abstract class RpcEvent implements RaftEvent {
    private long term;
    private ServerMetaData server;
    private String traceId;

    public RpcEvent (long term, ServerMetaData server) {
        this.traceId = UUID.randomUUID().toString();
        this.term = term;
        this.server = server;
    }
}
