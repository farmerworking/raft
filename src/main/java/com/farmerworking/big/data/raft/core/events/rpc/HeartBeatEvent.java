package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

public class HeartBeatEvent extends RpcEvent{
    public HeartBeatEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
