package com.farmerworking.big.data.raft.core;

import lombok.Data;

@Data
public class ServerMetaData {
    private int serverId;
    private int port;
    private String ip;
}
