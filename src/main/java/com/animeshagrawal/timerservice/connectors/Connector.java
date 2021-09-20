package com.animeshagrawal.timerservice.connectors;

import com.animeshagrawal.timerservice.model.Payload;

public interface Connector {
    public boolean produceEvent(Payload event);
}
