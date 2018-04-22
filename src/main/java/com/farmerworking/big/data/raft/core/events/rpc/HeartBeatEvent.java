package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

import com.farmerworking.big.data.raft.core.communication.RaftRpcBaseData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class HeartBeatEvent extends RpcEvent{
    public HeartBeatEvent(RaftRpcBaseData data, ServerMetaData server) {
        super(data.getTraceId(), data.getTerm(), server);
    }
}
