package com.animeshagrawal.timerservice.dbActions;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbConnector {
    public static DynamoDB dynamoDB = null;
    public static AmazonDynamoDB client = null;

    public DynamoDbConnector(String region)
    {
        if(dynamoDB!=null && client!=null)return;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();
        DynamoDbConnector.client = client;
        dynamoDB = new DynamoDB(client);
    }

    DynamoDbConnector(String serviceEndpoint, String region)
    {
        if(dynamoDB!=null && client!=null)return;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, region))
                .build();
        DynamoDbConnector.client = client;
        dynamoDB = new DynamoDB(client);
    }

    public DynamoDbConnector(String access_key_id, String secret_key_id, String region)
    {
        if(dynamoDB!=null && client!=null)return;
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_key_id);
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region).build();
        DynamoDbConnector.client = client;
        dynamoDB = new DynamoDB(client);
    }

    DynamoDB getConnection() {
        return DynamoDbConnector.dynamoDB;
    }
    AmazonDynamoDB getClient() {return DynamoDbConnector.client;}
}
