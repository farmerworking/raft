package com.farmerworking.big.data.raft.core.timeout.managers;

import java.util.Timer;

import com.farmerworking.big.data.raft.core.timeout.tasks.HeartBeatTimeoutTask;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

public class HeartBeatTimeoutManager {
    private Timer timer;
    private HeartBeatTimeoutTask timeoutTask;
    private long timeout;
    private EventBus eventBus;

    public HeartBeatTimeoutManager(EventBus eventBus, long timeout) {
        this.eventBus = eventBus;
        this.timeout = timeout;

        this.timer = new Timer("heart beat time out manager", true);
    }

    public void start() {
        Preconditions.checkArgument(timeoutTask == null);
        timeoutTask = new HeartBeatTimeoutTask(eventBus);
        // send heart beat right away
        timer.scheduleAtFixedRate(timeoutTask, 0, timeout);
    }

    public void stop() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }
}
