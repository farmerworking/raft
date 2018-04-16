package com.farmerworking.big.data.raft.core.timeout.tasks;

import com.farmerworking.big.data.raft.core.events.RaftEvent;
import com.farmerworking.big.data.raft.core.events.timeout.HeartBeatTimeoutEvent;
import com.google.common.eventbus.EventBus;

public class HeartBeatTimeoutTask extends TimeoutTask{
    public HeartBeatTimeoutTask(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected RaftEvent getEvent() {
        return new HeartBeatTimeoutEvent();
    }
}
