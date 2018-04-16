package com.farmerworking.big.data.raft.core.timeout.tasks;

import com.farmerworking.big.data.raft.core.events.RaftEvent;
import com.farmerworking.big.data.raft.core.events.timeout.ElectionTimeoutEvent;
import com.google.common.eventbus.EventBus;

public class ElectionTimeoutTask extends TimeoutTask{
    public ElectionTimeoutTask(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected RaftEvent getEvent() {
        return new ElectionTimeoutEvent();
    }
}
