package com.animeshagrawal.timerservice.constants;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.ArrayList;
import java.util.Set;

public class Constants {
    public static String KAFKA_BROKER;
    public static String KAFKA_SCHEMA_URL;

    public static String OFFSET_DYNAMO_NAME;

    public static String INTERNAL_SQS_QUEUE_NAME;
    public static String INTERNAL_SQS_QUEUE_URL;

    public static String DYNAMODB_NAME;
    public static String DYNAMODB_SECONDARY_INDEX;

    public static String AWS_SECRET_KEY;
    public static String AWS_ACCESS_KEY;
    public static String AWS_REGION;

    public static DynamoDB DYNAMODB;
    public static AmazonDynamoDB DYNAMODB_CLIENT;
    public static AmazonSQS SQS;
    public final static int SQS_CONSUMERS = 5;
    public static KafkaProducer<Object,Object> AVRO_PRODUCER = null;
    public static int PARTITIONS = 10;


    public static String getRandomString(int length){
        if(length <=1) length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return generatedString;
    }


    public static String generateArrayToQuotedCommaSeparatedStrings(Set<String> data){
        String result = "";
        if (data.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : data) {
                sb.append("'").append(s).append("'").append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

    public static String generateArrayToQuotedCommaSeparatedStrings(ArrayList<String> data){
        String result = "";
        if (data.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : data) {
                sb.append("'").append(s).append("'").append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

    public static int getPartitionHash(String data){
        int hash = 7;
        for (int i = 0; i < data.length(); i++) {
            hash = hash*31 + data.charAt(i);
        }
        return Math.abs(hash % PARTITIONS);
    }
}
