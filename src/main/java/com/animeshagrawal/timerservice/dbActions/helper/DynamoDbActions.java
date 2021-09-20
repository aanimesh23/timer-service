package com.animeshagrawal.timerservice.dbActions.helper;

import com.amazonaws.services.dynamodbv2.document.Attribute;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.dbActions.DynamoDbConnector;
import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadDynamoDb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DynamoDbActions {

    public Payload getMessage(String groupId) {
        Payload response;
        Condition groupIdCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(groupId));

        Map<String, Condition> keyConditions = new HashMap<>();
        keyConditions.put("groupId", groupIdCondition);

        QueryRequest request = new QueryRequest(Constants.DYNAMODB_NAME);
        request.setSelect(Select.ALL_ATTRIBUTES);
        request.setKeyConditions(keyConditions);
        request.setConsistentRead(true);
        QueryResult result = Constants.DYNAMODB_CLIENT.query(request);
        ArrayList<Map<String, AttributeValue>> rows = new ArrayList<>(result.getItems());
        while (result.getLastEvaluatedKey() != null) {
            request.setExclusiveStartKey(result.getLastEvaluatedKey());
            result = Constants.DYNAMODB_CLIENT.query(request);
            rows.addAll(result.getItems());
        }
        for(Map<String, AttributeValue> row: rows) {
            response = new Payload();
            response.setGroupId(row.getOrDefault("group_id", new AttributeValue().withS("")).getS());
            response.setMessageId(row.getOrDefault("message_id", new AttributeValue().withS("")).getS());
            String metaInfo = row.getOrDefault("meta_info", new AttributeValue().withS("")).getS();
            Map<String, String> valMap = new Gson().fromJson(
                    metaInfo, new TypeToken<HashMap<String, String>>() {
                    }.getType()
            );
            response.setMetaInfo(valMap);
            List<AttributeValue> payloads = row.getOrDefault("payloads", new AttributeValue().withL(new ArrayList<>())).getL();
            List<String> al = new ArrayList<>();
            for(AttributeValue av: payloads) {
                al.add(av.getS());
            }
            response.setPayloads(al);
            response.setProductName(row.getOrDefault("product_name", new AttributeValue().withS("")).getS());
            response.setDestinationType(row.getOrDefault("destination_type", new AttributeValue().withS("")).getS());
            response.setDestination(row.getOrDefault("destination", new AttributeValue().withS("")).getS());
            response.setSource(row.getOrDefault("source", new AttributeValue().withS("")).getS());
            response.setSourceId(row.getOrDefault("source_id", new AttributeValue().withS("")).getS());
            response.setDate(row.getOrDefault("date", new AttributeValue().withS("")).getS());
            response.setEpoch(Long.parseLong(row.getOrDefault("epoch", new AttributeValue().withN("0")).getN()));
            response.setOffset(Long.parseLong(row.getOrDefault("partition", new AttributeValue().withN("0")).getN()));
            response.setOffset(Long.parseLong(row.getOrDefault("offset", new AttributeValue().withN("0")).getN()));
            response.setExpiry(Long.parseLong(row.getOrDefault("expiry", new AttributeValue().withN("0")).getN()));
            response.setProduced(row.getOrDefault("produced", new AttributeValue().withBOOL(false)).getBOOL());
            return response;
        }
        return null;
    }

    public ArrayList<Payload> getMessage(String date, String epoch, String lastEpoch, int partition) {
        ArrayList<Payload> responses = new ArrayList<>();
        Condition groupIdCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(date));
        Condition sortKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(new AttributeValue().withN(lastEpoch), new AttributeValue().withN(epoch));

        Map<String, Condition> keyConditions = new HashMap<>();
        keyConditions.put("date", groupIdCondition);
        keyConditions.put("epoch", sortKeyCondition);

        Condition filterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withBOOL(false));
        Condition filterConditionPartition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN(String.valueOf(partition)));

        Map<String, Condition> filterConditions = new HashMap<>();
        filterConditions.put("produced", filterCondition);
        filterConditions.put("partition", filterConditionPartition);

        QueryRequest request = new QueryRequest(Constants.DYNAMODB_NAME);
        request.setIndexName(Constants.DYNAMODB_SECONDARY_INDEX);
        request.setSelect(Select.ALL_ATTRIBUTES);
        request.setKeyConditions(keyConditions);
        request.setScanIndexForward(true);
        request.setQueryFilter(filterConditions);
        QueryResult result = Constants.DYNAMODB_CLIENT.query(request);
        ArrayList<Map<String, AttributeValue>> rows = new ArrayList<>(result.getItems());
        while (result.getLastEvaluatedKey() != null) {
            request.setExclusiveStartKey(result.getLastEvaluatedKey());
            result = Constants.DYNAMODB_CLIENT.query(request);
            rows.addAll(result.getItems());
        }
        for(Map<String, AttributeValue> row: rows) {
            Payload response = new Payload();
            response.setGroupId(row.getOrDefault("group_id", new AttributeValue().withS("")).getS());
            response.setMessageId(row.getOrDefault("message_id", new AttributeValue().withS("")).getS());
            String metaInfo = row.getOrDefault("meta_info", new AttributeValue().withS("")).getS();
            Map<String, String> valMap = new Gson().fromJson(
                    metaInfo, new TypeToken<HashMap<String, String>>() {
                    }.getType()
            );
            response.setMetaInfo(valMap);
            List<AttributeValue> payloads = row.getOrDefault("payloads", new AttributeValue().withL(new ArrayList<>())).getL();
            List<String> al = new ArrayList<>();
            for(AttributeValue av: payloads) {
                al.add(av.getS());
            }
            response.setPayloads(al);
            response.setProductName(row.getOrDefault("product_name", new AttributeValue().withS("")).getS());
            response.setDestinationType(row.getOrDefault("destination_type", new AttributeValue().withS("")).getS());
            response.setDestination(row.getOrDefault("destination", new AttributeValue().withS("")).getS());
            response.setSource(row.getOrDefault("source", new AttributeValue().withS("")).getS());
            response.setSourceId(row.getOrDefault("source_id", new AttributeValue().withS("")).getS());
            response.setDate(row.getOrDefault("date", new AttributeValue().withS("")).getS());
            response.setEpoch(Long.parseLong(row.getOrDefault("epoch", new AttributeValue().withN("0")).getN()));
            response.setOffset(Long.parseLong(row.getOrDefault("partition", new AttributeValue().withN("0")).getN()));
            response.setOffset(Long.parseLong(row.getOrDefault("offset", new AttributeValue().withN("0")).getN()));
            response.setExpiry(Long.parseLong(row.getOrDefault("expiry", new AttributeValue().withN("0")).getN()));
            response.setProduced(row.getOrDefault("produced", new AttributeValue().withBOOL(false)).getBOOL());
            responses.add(response);
        }
        return responses;
    }


    public void publishMessage(Payload payloadObj, long expiry, boolean produced) {
        PayloadDynamoDb payloadDynamoDb = new PayloadDynamoDb();
        payloadDynamoDb.setGroup_id(payloadObj.getGroupId());
        payloadDynamoDb.setMessage_id(payloadObj.getMessageId());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String metaInfo = gson.toJson(payloadObj.getMetaInfo());
        payloadDynamoDb.setMeta_info(metaInfo);
        payloadDynamoDb.setPayloads(payloadObj.getPayloads());
        payloadDynamoDb.setProduct_name(payloadObj.getProductName());
        payloadDynamoDb.setDestination(payloadObj.getDestination());
        payloadDynamoDb.setDestination_type(payloadObj.getDestinationType());
        payloadDynamoDb.setSource(payloadObj.getSource());
        payloadDynamoDb.setSource_id(payloadObj.getSourceId());
        payloadDynamoDb.setDate(payloadObj.getDate());
        payloadDynamoDb.setEpoch(payloadObj.getEpoch());
        payloadDynamoDb.setPartition(payloadObj.getPartition());
        payloadDynamoDb.setOffset(payloadObj.getOffset());
        payloadDynamoDb.setExpiry(expiry);
        payloadDynamoDb.setProduced(produced);

        builder = new GsonBuilder();
        gson = builder.create();
        String finalPayload = gson.toJson(payloadDynamoDb);
        Constants.DYNAMODB.getTable(Constants.DYNAMODB_NAME).putItem(Item.fromJSON(finalPayload));
    }
}
