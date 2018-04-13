package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

public class HeartBeatRequestTimeoutEvent extends RpcEvent {
    public HeartBeatRequestTimeoutEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
