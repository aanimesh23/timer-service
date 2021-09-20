package com.animeshagrawal.timerservice.constants;

import lombok.Getter;

@Getter
public enum AvroSchemas {

    AVRO_PRODUCER_VALUE_SCHEMA("{\n" +
            "  \"namespace\": \"time.service.events\",\n" +
            "  \"type\": \"record\",\n" +
            "  \"name\": \"TimeServicePayload\",\n" +
            "  \"fields\": [\n" +
            "    {\n" +
            "      \"name\": \"groupId\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"messageId\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"metaInfo\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"payloads\",\n" +
            "      \"type\": \"array\",\n" +
            "      \"items\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"productName\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"destinationType\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"destination\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"source\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"sourceId\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"date\",\n" +
            "      \"type\": \"string\",\n" +
            "      \"default\": \"null\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"epoch\",\n" +
            "      \"type\": \"long\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"offset\",\n" +
            "      \"type\": \"long\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"expiry\",\n" +
            "      \"type\": \"long\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"),
    AVRO_PRODUCER_KEY_SCHEMA("{\n" +
            "  \"namespace\": \"advisory.log.events\",\n" +
            "  \"type\": \"record\",\n" +
            "  \"name\": \"AdvisoryEventLogsKey\",\n" +
            "  \"fields\": [\n" +
            "    {\n" +
            "      \"name\": \"groupId\",\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}");

    String value;
    AvroSchemas(String value){
        this.value = value;
    }
}
