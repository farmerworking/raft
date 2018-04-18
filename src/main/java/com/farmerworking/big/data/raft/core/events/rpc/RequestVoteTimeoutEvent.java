package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class RequestVoteTimeoutEvent extends RpcEvent {
    public RequestVoteTimeoutEvent(long term, ServerMetaData server) {
        super(term, server);
    }
}
