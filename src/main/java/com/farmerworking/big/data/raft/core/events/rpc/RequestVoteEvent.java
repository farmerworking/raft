package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class RequestVoteEvent extends RpcEvent{
    public RequestVoteEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
