package com.farmerworking.big.data.raft.core.timeout.tasks;

import com.farmerworking.big.data.raft.core.events.RaftEvent;
import com.google.common.eventbus.EventBus;

public class RpcTimeoutTask extends TimeoutTask{
    private RaftEvent event;

    public RpcTimeoutTask(EventBus eventBus, RaftEvent event) {
        super(eventBus);
        this.event = event;
    }

    @Override
    protected RaftEvent getEvent() {
        return event;
    }
}
