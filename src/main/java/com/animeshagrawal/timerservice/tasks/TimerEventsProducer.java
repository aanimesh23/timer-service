package com.animeshagrawal.timerservice.tasks;

import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.animeshagrawal.timerservice.connectors.KafkaProducerConnector;
import com.animeshagrawal.timerservice.connectors.SqsProducerConnector;
import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.constants.SupportedDestinations;
import com.animeshagrawal.timerservice.dbActions.helper.DynamoDbActions;
import com.animeshagrawal.timerservice.model.Offset;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.google.gson.Gson;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class TimerEventsProducer {

    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");
    Date jobScheduleTime;
    DynamoDbActions dynamoDbActions = new DynamoDbActions();
    int partition;
    TimerEventsProducer(Date jobScheduleTime, int partition) {
        this.jobScheduleTime = jobScheduleTime;
        this.partition = partition;
    }
    @Trace(dispatcher=true)
    void init() {
        log.info("Running TimerEventsProducer scheduler task for partition: " + this.partition);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata")));
        calendar.setTime(this.jobScheduleTime);

        Offset offset = Offset.getOffset(this.partition);;
        assert offset != null;
        long endEpoch = calendar.getTimeInMillis() / 1000L;
        long startEpoch = offset.getMaxEpoch() + 1;

        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        LocalDate localDate = LocalDateTime.ofInstant(calendar.toInstant(), zid).toLocalDate();
        String date = localDate.toString();

        log.info("fetching: " + date + " start epoch: " + String.valueOf(startEpoch) + " end epoch: " + String.valueOf(endEpoch) + " partition: " + String.valueOf(this.partition));
        List<Payload> events = dynamoDbActions.getMessage(date, String.valueOf(endEpoch), String.valueOf(startEpoch), this.partition);
        log.info(events.size() + " events to be produced");
        for(Payload event: events) {
            PayloadLogger payloadLogger = new PayloadLogger(event, "Picked by scheduler",
                    PayloadLogger.Status.PICKED_BY_SCHEDULER.getValue());
            eventLogger.info("{}", payloadLogger);
            try {
                if (event.getDestinationType().equalsIgnoreCase(SupportedDestinations.KAFKA.getName())) {
                    KafkaProducerConnector kafkaProducerConnector = new KafkaProducerConnector();
                    if (!kafkaProducerConnector.produceEvent(event)) {
                        throw new RuntimeException("Error producing event to Destination Kafka Topic: " + event.getDestination());
                    }
                } else if (event.getDestinationType().equalsIgnoreCase(SupportedDestinations.SQS.getName())) {
                    SqsProducerConnector sqsProducerConnector = new SqsProducerConnector();
                    if (!sqsProducerConnector.produceEvent(event)) {
                        throw new RuntimeException("Error producing event to Destination SQS: " + event.getDestination());
                    }
                } else if (event.getDestinationType().equalsIgnoreCase(SupportedDestinations.API.getName())) {
                    SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                            .withQueueUrl(Constants.INTERNAL_SQS_QUEUE_URL)
                            .withMessageBody(new Gson().toJson(event));
                    Constants.SQS.sendMessage(sendMessageStandardQueue);
                    payloadLogger = new PayloadLogger(event, "Sent to internal queue",
                            PayloadLogger.Status.INTERNAL_QUEUE.getValue());
                    eventLogger.info("{}", payloadLogger);
                }
                event.setProduced(true);
                dynamoDbActions.publishMessage(event, event.getExpiry(), true);
                offset = Offset.getOffset(this.partition);
                if(offset == null) {
                    log.error("Error in managing offset for event: " + event);
                }
                else {
                    offset.incOffsetCurr();
                    offset.setMaxEpoch(event.getEpoch());
                    offset.setLastGroupId(event.getGroupId());
                    offset.setOffset();
                }
            } catch (Exception e) {
                log.error("Error producing event : " + e);
                payloadLogger = new PayloadLogger(event, e.getMessage(),
                        PayloadLogger.Status.DROPPED.getValue());
                eventLogger.info("{}", payloadLogger);
            }
        }
    }
}
