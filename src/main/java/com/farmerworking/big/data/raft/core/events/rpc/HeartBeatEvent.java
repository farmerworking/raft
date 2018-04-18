package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class HeartBeatEvent extends RpcEvent{
    public HeartBeatEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
