package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;
import lombok.Data;

@Data
public class RequestVoteReplyEvent extends RpcEvent {
    boolean voteGranted;

    public RequestVoteReplyEvent(long term, ServerMetaData server, boolean voteGranted) {
        super(term, server);
        this.voteGranted = voteGranted;
    }
}
