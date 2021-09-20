package com.animeshagrawal.timerservice.dbActions;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SqsConnector {
    public static AmazonSQS sqsClient = null;

    public SqsConnector(String region)
    {
        if(sqsClient!=null)return;
        SqsConnector.sqsClient = AmazonSQSClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    SqsConnector(String serviceEndpoint, String region)
    {
        if(sqsClient!=null)return;
        SqsConnector.sqsClient = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, region))
                .build();
    }

    public SqsConnector(String access_key_id, String secret_key_id, String region)
    {
        if(sqsClient!=null)return;
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_key_id);
        SqsConnector.sqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region).build();
    }

    AmazonSQS getClient() {
        return SqsConnector.sqsClient;
    }
}
