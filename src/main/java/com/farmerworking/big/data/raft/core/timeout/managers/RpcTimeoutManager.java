package com.farmerworking.big.data.raft.core.timeout.managers;

import java.util.Map;
import java.util.Timer;

import com.farmerworking.big.data.raft.core.events.rpc.RpcEvent;
import com.farmerworking.big.data.raft.core.timeout.tasks.RpcTimeoutTask;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

public class RpcTimeoutManager {
    private Timer timer;
    private long timeout;
    private EventBus eventBus;
    private Map<String, RpcTimeoutTask> tasksMap;

    public RpcTimeoutManager(EventBus eventBus, long timeout) {
        this.timeout = timeout;
        this.eventBus = eventBus;

        this.timer = new Timer("rpc time out manager", true);
        this.tasksMap = Maps.newConcurrentMap();
    }

    public void start(RpcEvent rpcEvent) {
        RpcTimeoutTask timeoutTask = new RpcTimeoutTask(eventBus, rpcEvent);

        tasksMap.put(rpcEvent.getTraceId(), timeoutTask);
        timer.schedule(timeoutTask, timeout);
    }

    public void stop(String identifier) {
        RpcTimeoutTask timeoutTask = tasksMap.get(identifier);

        if (timeoutTask != null) {
            timeoutTask.cancel();
            tasksMap.remove(identifier);
        }
    }

    public void clear() {
        for(RpcTimeoutTask task : tasksMap.values()) {
            task.cancel();
        }

        tasksMap.clear();
    }
}
