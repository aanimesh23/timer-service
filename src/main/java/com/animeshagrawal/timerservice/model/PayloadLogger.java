package com.animeshagrawal.timerservice.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Getter
@Setter
public class PayloadLogger {
    String groupId; //Primary key
    String messageId; //Sort key
    Map<String, String> metaInfo;
    List<String> payloads;
    String productName;
    String destinationType;
    String destination;
    String source;
    String sourceId;
    String date; //Secondary Primary
    long epoch; //Secondary Sort
    boolean produced;
    String time;
    String message;
    String status;

    public PayloadLogger(Payload event, String message, String status) {
        this.groupId = event.getGroupId();
        this.messageId = event.getMessageId();
        this.metaInfo = event.getMetaInfo();
        this.payloads = event.getPayloads();
        this.productName = event.getProductName();
        this.destinationType = event.getDestinationType();
        this.destination = event.getDestination();
        this.source = event.getSource();
        this.sourceId = event.getSourceId();
        this.date = event.getDate();
        this.epoch = event.getEpoch();
        this.produced = event.isProduced();
        this.message = message;
        this.status = status;
        ZonedDateTime dateTimeZone = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.time= dateTimeZone.format(ISO_OFFSET_DATE_TIME);
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Getter
    public enum Status {
        RECEIVED("RECEIVED"),
        UPDATED("UPDATED"),
        PICKED_BY_SCHEDULER("PICKED_BY_SCHEDULER"),
        INTERNAL_QUEUE("INTERNAL_QUEUE"),
        PRODUCED("PRODUCED"),
        DROPPED("DROPPED"),
        RETRY("RETRY");

        private final String value;

        Status(String value) {
            this.value = value;

        }
    }
}
