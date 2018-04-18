package com.farmerworking.big.data.raft.core.events.timeout;

import com.farmerworking.big.data.raft.core.events.RaftEvent;
import lombok.Data;

@Data
public class HeartBeatTimeoutEvent implements RaftEvent {
}
