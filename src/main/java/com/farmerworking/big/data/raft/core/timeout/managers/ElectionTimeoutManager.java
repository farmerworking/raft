package com.farmerworking.big.data.raft.core.timeout.managers;

import java.util.Random;
import java.util.Timer;

import com.farmerworking.big.data.raft.core.timeout.tasks.ElectionTimeoutTask;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * thread-safe
 */
public class ElectionTimeoutManager {
    private Random random = new Random();

    private Timer timer;
    private ElectionTimeoutTask timeoutTask;
    private long timeout;
    private int randomTimeoutRange;
    private EventBus eventBus;

    public ElectionTimeoutManager(EventBus eventBus, long timeout, int randomTimeoutRange) {
        this.eventBus = eventBus;
        this.timeout = timeout;
        this.randomTimeoutRange = randomTimeoutRange;

        this.timer = new Timer("election time out manager", true);
    }

    public void start() {
        stop();
        Preconditions.checkArgument(timeoutTask == null);
        timeoutTask = new ElectionTimeoutTask(eventBus);
        timer.schedule(timeoutTask, nextTimeout());
    }

    public void stop() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

    private long nextTimeout() {
        return randomTimeoutRange == 0 ? timeout : timeout + random.nextInt(randomTimeoutRange);
    }
}
