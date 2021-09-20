package com.animeshagrawal.timerservice.tasks;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.animeshagrawal.timerservice.configs.AppConfigs;
import com.animeshagrawal.timerservice.constants.Constants;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SqsConsumer implements Runnable {

    private AppConfigs appConfigs;

    public SqsConsumer(AppConfigs appConfigs) {
        this.appConfigs = appConfigs;
    }

    @Override
    public void run() {
        log.info("Starting SQS Consumer");
        while(true) {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(Constants.INTERNAL_SQS_QUEUE_URL)
                    .withWaitTimeSeconds(10)
                    .withMaxNumberOfMessages(10);
            List<Message> sqsMessages = Constants.SQS.receiveMessage(receiveMessageRequest).getMessages();
            log.info("Messages Received: " + sqsMessages.size());
            for(Message m : sqsMessages) {
                SqsMessageHandler sqsMessageHandler = new SqsMessageHandler();
                log.info("Message Received: " + m.getBody());
                if (!sqsMessageHandler.handelMessage(m.getBody())) {
                    log.info("Could not handle message");
                } else {
                    deleteMessage(m);
                }
            }
        }
    }

    public void deleteMessage(Message m) {
        Constants.SQS.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(Constants.INTERNAL_SQS_QUEUE_URL)
                .withReceiptHandle(m.getReceiptHandle()));
    }
}
