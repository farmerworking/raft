package com.farmerworking.big.data.raft.core.events.rpc;

import com.farmerworking.big.data.raft.core.ServerMetaData;
import com.farmerworking.big.data.raft.core.communication.VoteReplyData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class RequestVoteReplyEvent extends RpcEvent {
    boolean voteGranted;

    public RequestVoteReplyEvent(VoteReplyData data, ServerMetaData server) {
        super(data.getTraceId(), data.getTerm(), server);
        this.voteGranted = data.isVoteGranted();
    }
}
