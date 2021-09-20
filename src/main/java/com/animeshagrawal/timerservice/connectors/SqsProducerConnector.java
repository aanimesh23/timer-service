package com.animeshagrawal.timerservice.connectors;

import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.google.gson.Gson;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class SqsProducerConnector implements Connector  {
    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");
    @Trace(dispatcher=true)
    public boolean produceEvent(Payload event) {
        try {
            SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                    .withQueueUrl(event.getDestination())
                    .withMessageBody(new Gson().toJson(event));
            Constants.SQS.sendMessage(sendMessageStandardQueue);
            PayloadLogger payloadLogger = new PayloadLogger(event, "Sent to API",
                    PayloadLogger.Status.PRODUCED.getValue());
            eventLogger.info("{}", payloadLogger);
            return true;
        } catch (Exception e) {
            log.error("Error producing event to Internal SQS: " + e);
            PayloadLogger payloadLogger = new PayloadLogger(event, e.getMessage(),
                    PayloadLogger.Status.DROPPED.getValue());
            eventLogger.info("{}", payloadLogger);
            return false;
        }
    }
}
