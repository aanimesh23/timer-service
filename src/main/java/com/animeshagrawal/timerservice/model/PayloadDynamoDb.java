package com.animeshagrawal.timerservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class PayloadDynamoDb {
    String group_id;
    String message_id;
    String meta_info;
    List<String> payloads;
    String product_name;
    String destination_type;
    String destination;
    String source;
    String source_id;
    String date;
    long epoch;
    long partition;
    long offset;
    long expiry;
    boolean produced;

    public PayloadDynamoDb() {
        this.payloads = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PayloadDynamoDb{" +
                "group_id='" + group_id + '\'' +
                ", message_id='" + message_id + '\'' +
                ", meta_info='" + meta_info + '\'' +
                ", payloads=" + payloads +
                ", product_name='" + product_name + '\'' +
                ", destination_type='" + destination_type + '\'' +
                ", destination='" + destination + '\'' +
                ", source='" + source + '\'' +
                ", source_id='" + source_id + '\'' +
                ", date='" + date + '\'' +
                ", epoch=" + epoch +
                ", partition=" + partition +
                ", offset=" + offset +
                ", expiry=" + expiry +
                ", produced=" + produced +
                '}';
    }
}
