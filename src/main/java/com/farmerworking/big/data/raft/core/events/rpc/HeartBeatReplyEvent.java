package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

public class HeartBeatReplyEvent extends RpcEvent {
    public HeartBeatReplyEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
