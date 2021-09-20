package com.animeshagrawal.timerservice.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Payload {
    @SerializedName("group_id")
    String groupId; //Primary key
    @SerializedName("message_id")
    String messageId; //Sort key
    Map<String, String> metaInfo;
    List<String> payloads;
    @SerializedName("product_name")
    String productName;
    @SerializedName("destination_type")
    String destinationType;
    String destination;
    String source;
    @SerializedName("source_id")
    String sourceId;
    String date; //Secondary Primary
    long epoch; //Secondary Sort
    long offset;
    long partition;
    long expiry;
    boolean produced;

    @Override
    public String toString() {
        return "Payload{" +
                "groupId='" + groupId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", metaInfo=" + metaInfo +
                ", payloads=" + payloads +
                ", productName='" + productName + '\'' +
                ", destinationType='" + destinationType + '\'' +
                ", destination='" + destination + '\'' +
                ", source='" + source + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", date='" + date + '\'' +
                ", epoch=" + epoch +
                ", offset=" + offset +
                ", partition=" + partition +
                ", expiry=" + expiry +
                ", produced=" + produced +
                '}';
    }
}
