package com.animeshagrawal.timerservice.tasks;

import com.animeshagrawal.timerservice.connectors.ApiProducerConnector;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SqsMessageHandler {
    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");

    @Trace(dispatcher=true)
    public boolean handelMessage(String message) {
        Payload payload = null;
        try {
            payload = new Gson().fromJson(message, new TypeToken<Payload>() {}.getType());
        } catch (Exception e) {
            log.error("Error parsing event : " + message);
        }
        if (payload == null) {
            return false;
        }
        try {
            ApiProducerConnector apiProducerConnector = new ApiProducerConnector();
            if (!apiProducerConnector.produceEvent(payload)) {
                PayloadLogger payloadLogger = new PayloadLogger(payload, "Error from API",
                        PayloadLogger.Status.RETRY.getValue());
                eventLogger.info("{}", payloadLogger);
                throw new RuntimeException("Could not produce event");
            }
            return true;
        } catch (Exception e) {
            PayloadLogger payloadLogger = new PayloadLogger(payload, "Error from API",
                    PayloadLogger.Status.RETRY.getValue());
            eventLogger.info("{}", payloadLogger);
            log.error("Error calling post api : ", e);
        }
        return false;
    }
}
