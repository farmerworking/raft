package com.farmerworking.big.data.raft.core.timeout.tasks;

import java.util.TimerTask;

import com.farmerworking.big.data.raft.core.events.RaftEvent;
import com.google.common.eventbus.EventBus;

public abstract class TimeoutTask extends TimerTask {
    private EventBus eventBus;

    public TimeoutTask(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void run() {
        eventBus.post(getEvent());
    }

    protected abstract RaftEvent getEvent();
}
