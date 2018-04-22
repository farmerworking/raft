package com.farmerworking.big.data.raft.core;

import com.google.common.eventbus.EventBus;
import lombok.Data;

@Data
public class ServerMetaData {
    private int serverId;
    private int port;
    private String ip;
    private EventBus eventBus;

    public ServerMetaData(int serverId, int port, String ip) {
        this.serverId = serverId;
        this.port = port;
        this.ip = ip;
        eventBus = new EventBus();
    }
}
