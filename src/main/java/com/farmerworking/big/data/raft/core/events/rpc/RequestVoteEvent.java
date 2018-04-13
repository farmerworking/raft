package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

public class RequestVoteEvent extends RpcEvent{
    public RequestVoteEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
