package com.animeshagrawal.timerservice.api;

import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.constants.SupportedDestinations;
import com.animeshagrawal.timerservice.dbActions.helper.DynamoDbActions;
import com.animeshagrawal.timerservice.model.Offset;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadApiInput;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.newrelic.api.agent.Trace;
import exceptions.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.animeshagrawal.timerservice.constants.Constants.PARTITIONS;
import static com.animeshagrawal.timerservice.constants.Constants.getPartitionHash;

@Slf4j
@RequestMapping("api/v1/")
@RestController
@CrossOrigin
public class ApiController {
    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");
    DynamoDbActions dynamoDbActions = new DynamoDbActions();
    //KnockKnock
    @Trace(dispatcher=true)
    @GetMapping(path = "knockknock")
    public ResponseEntity<String> knockknock() {
        log.debug("knockknock API called for Timer Service");
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    //get offset
    @Trace(dispatcher=true)
    @GetMapping("offset/{partition}/")
    public Offset getOffset(@PathVariable String partition) {
        int partitionId = Integer.parseInt(partition);
        return Offset.getOffset(partitionId);
    }

    //get offset
    @Trace(dispatcher=true)
    @GetMapping("offsets/")
    public ArrayList<Offset> getOffsets() {
        ArrayList<Offset> resp = new ArrayList<>();
        for(int i = 0; i < PARTITIONS; i ++) {
            resp.add(Offset.getOffset(i));
        }
        return resp;
    }

    //Post message
    @Trace(dispatcher=true)
    @PostMapping("message/")
    public ResponseEntity<Payload> addMessage(@Valid @RequestBody PayloadApiInput input) {
        Payload p;
        if(input.getGroup_id() != null && !input.getGroup_id().equalsIgnoreCase("")) {
            p = dynamoDbActions.getMessage(input.getGroup_id());
            if (p == null) {
                p = new Payload();
                p.setGroupId(input.getGroup_id());
                p.setMessageId(input.getGroup_id() + "_message");
                if (input.getMetaInfo() != null && input.getMetaInfo().keySet().size() > 0) {
                    p.setMetaInfo(input.getMetaInfo());
                } else {
                    p.setMetaInfo(new HashMap<>());
                }
                p.setPayloads(new ArrayList<>());
                p.getPayloads().add(input.getPayload());
                p.setProductName(input.getProduct_name());
                if (SupportedDestinations.containsDestination(input.getDestination_type())) {
                    p.setDestinationType(input.getDestination_type());
                } else {
                    throw new ApiRequestException("Invalid destination_type");
                }
                p.setDestination(input.getDestination());
                p.setSource(input.getSource());
                p.setSourceId(input.getSource_id());
                long epoch = input.getEpoch();
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch * 1000L), ZoneId.of("Asia/Kolkata"));
                LocalDateTime expDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).
                        toLocalDateTime().plusDays(28);
                long expiry = expDate.atZone(ZoneId.of("Asia/Kolkata")).toEpochSecond();
                p.setEpoch(epoch);
                p.setDate(date.toLocalDate().toString());
                p.setExpiry(expiry);
                int partition = getPartitionHash(input.getSource() + "__" + input.getProduct_name());
                p.setPartition(partition);
                Offset offset = Offset.getOffset(partition);
                assert offset != null;
                p.setOffset(offset.getEndOffset() + 1);
                offset.incOffsetEnd();
                dynamoDbActions.publishMessage(p, expiry, false);
                offset.setOffset();
                PayloadLogger payloadLogger = new PayloadLogger(p, "Event Received", PayloadLogger.Status.RECEIVED.getValue());
                eventLogger.info("{}", payloadLogger);
            } else {
                //Make Immutable
                p.getPayloads().add(input.getPayload());
                p.setProductName(input.getProduct_name());
                if (SupportedDestinations.containsDestination(input.getDestination_type())) {
                    p.setDestinationType(input.getDestination_type());
                } else {
                    throw new ApiRequestException("Invalid destination_type");
                }
                p.setDestination(input.getDestination());
                p.setSource(input.getSource());
                p.setSourceId(input.getSource_id());
                long epoch = input.getEpoch();
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch*1000L), ZoneId.of("Asia/Kolkata"));
                LocalDateTime expDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).
                        toLocalDateTime().plusDays(28);
                long expiry = expDate.atZone(ZoneId.of("Asia/Kolkata")).toEpochSecond();
                p.setEpoch(epoch);
                p.setDate(date.toLocalDate().toString());
                p.setExpiry(expiry);
                int partition = getPartitionHash(input.getSource() + "__" + input.getProduct_name());
                p.setPartition(partition);
                dynamoDbActions.publishMessage(p, expiry, false);
                PayloadLogger payloadLogger = new PayloadLogger(p, "Event Received", PayloadLogger.Status.UPDATED.getValue());
                eventLogger.info("{}", payloadLogger);
            }
        } else {
            p = new Payload();
            String group_id = input.getSource() + "__" + input.getProduct_name() + Constants.getRandomString(-1);
            p.setGroupId(group_id);
            p.setMessageId(group_id + "_message");
            if (input.getMetaInfo() != null && input.getMetaInfo().keySet().size() > 0) {
                p.setMetaInfo(input.getMetaInfo());
            } else {
                p.setMetaInfo(new HashMap<>());
            }
            p.setPayloads(new ArrayList<>());
            p.getPayloads().add(input.getPayload());
            p.setProductName(input.getProduct_name());
            if (SupportedDestinations.containsDestination(input.getDestination_type())) {
                p.setDestinationType(input.getDestination_type());
            } else {
                throw new ApiRequestException("Invalid destination_type");
            }
            p.setDestination(input.getDestination());
            p.setSource(input.getSource());
            p.setSourceId(input.getSource_id());
            long epoch = input.getEpoch();
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch * 1000L), ZoneId.of("Asia/Kolkata"));
            LocalDateTime expDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).
                    toLocalDateTime().plusDays(28);
            long expiry = expDate.atZone(ZoneId.of("Asia/Kolkata")).toEpochSecond();
            p.setEpoch(epoch);
            p.setDate(date.toLocalDate().toString());
            p.setExpiry(expiry);
            int partition = getPartitionHash(input.getSource() + "__" + input.getProduct_name());
            p.setPartition(partition);
            Offset offset = Offset.getOffset(partition);
            assert offset != null;
            p.setOffset(offset.getEndOffset() + 1);
            offset.incOffsetEnd();
            dynamoDbActions.publishMessage(p, expiry, false);
            offset.setOffset();
            PayloadLogger payloadLogger = new PayloadLogger(p, "Event Received", PayloadLogger.Status.RECEIVED.getValue());
            eventLogger.info("{}", payloadLogger);
        }
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    //Get message
    @Trace(dispatcher=true)
    @GetMapping("message/{group_id}/")
    public ResponseEntity<Payload> getMessage(@PathVariable String group_id) {
        Payload p = dynamoDbActions.getMessage(group_id);
        if (p == null) {
            throw new ApiRequestException(group_id + " message not found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(p, HttpStatus.OK);
    }
}
