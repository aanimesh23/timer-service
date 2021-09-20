package com.animeshagrawal.timerservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class PayloadApiInput {
    String group_id;
    @NotEmpty(message = "Please provide a String/Base64 value of payloads")
    String payload;
    @NotEmpty(message = "Please provide a String value of product_name (eg: \"MF\")")
    String product_name;
    @NotEmpty(message = "Please provide a String value of destination_type (eg: \"SQS\",\"kafka\",\"API\")")
    String destination_type;
    @NotEmpty(message = "Please provide a String value of destination")
    String destination;
    @NotEmpty(message = "Please provide a String value of source")
    String source;
    String source_id;
    @NotNull(message = "Please provide a long value of epoch seconds")
    long epoch;
    Map<String, String> metaInfo;

    @Override
    public String toString() {
        return "PayloadApiInput{" +
                "group_id='" + group_id + '\'' +
                ", payload='" + payload + '\'' +
                ", product_name='" + product_name + '\'' +
                ", destination_type='" + destination_type + '\'' +
                ", destination='" + destination + '\'' +
                ", source='" + source + '\'' +
                ", source_id='" + source_id + '\'' +
                ", epoch=" + epoch +
                ", metaInfo=" + metaInfo +
                '}';
    }
}
