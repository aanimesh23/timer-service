package com.animeshagrawal.timerservice.connectors;

import com.animeshagrawal.timerservice.constants.AvroSchemas;
import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.google.gson.Gson;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class KafkaProducerConnector implements Connector {
    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");
    @Trace(dispatcher=true)
    public boolean produceEvent(Payload event) {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(AvroSchemas.AVRO_PRODUCER_VALUE_SCHEMA.getValue());
        GenericRecord response = new GenericData.Record(schema);
        response.put("groupId",event.getGroupId());
        response.put("messageId",event.getMessageId() == null);
        response.put("metaInfo", new Gson().toJson(event.getMetaInfo()));
        response.put("payloads",event.getPayloads());
        response.put("productName",event.getProductName());
        response.put("destinationType",event.getDestinationType());
        response.put("destination",event.getDestination());
        response.put("source",event.getSource());
        response.put("sourceId",event.getSourceId());
        response.put("date",event.getDate());
        response.put("epoch",event.getEpoch());
        response.put("offset",event.getOffset());
        response.put("expiry",event.getExpiry());


        Schema.Parser keyParser = new Schema.Parser();
        Schema keySchema = keyParser.parse(AvroSchemas.AVRO_PRODUCER_KEY_SCHEMA.getValue());
        GenericRecord keyRecord = new GenericData.Record(keySchema);
        keyRecord.put("groupId",event.getGroupId());
        ProducerRecord<Object, Object> transformedRecord = new ProducerRecord<>(
                event.getDestination(), keyRecord, response);
        try {
            //log.info("producing event {}",transformedRecord);
            Constants.AVRO_PRODUCER.send(transformedRecord);
            log.debug("KafkaProducerConnector event processed successfully");
            log.debug(String.valueOf(event));
            PayloadLogger payloadLogger = new PayloadLogger(event, "Sent to API",
                    PayloadLogger.Status.PRODUCED.getValue());
            eventLogger.info("{}", payloadLogger);
            return true;
        } catch (Exception e) {
            log.error("KafkaProducerConnector Error while event data into kafka topic");
            log.error(String.valueOf(event));
            log.error("", e);
            PayloadLogger payloadLogger = new PayloadLogger(event, e.getMessage(),
                    PayloadLogger.Status.DROPPED.getValue());
            eventLogger.info("{}", payloadLogger);
        }
        return false;
    }
}
