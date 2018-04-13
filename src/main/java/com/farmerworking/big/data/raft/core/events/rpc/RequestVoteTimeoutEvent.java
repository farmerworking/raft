package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

public class RequestVoteTimeoutEvent extends RpcEvent {
    public RequestVoteTimeoutEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
