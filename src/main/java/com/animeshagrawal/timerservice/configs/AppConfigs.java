package com.animeshagrawal.timerservice.configs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@NoArgsConstructor
@Configuration
public class AppConfigs {

    @Value("${kafka.bootstrap.servers}")
    private String kafkaServer;
    @Value("${kafka.schema.registry.url}")
    private String kafkaSchemaUrl;

    @Value("${internal.sqs.queue.name}")
    private String internalQueueName;
    @Value("${internal.sqs.queue.url}")
    private String internalQueueUrl;

    @Value("${dynamodb.table.name.offset}")
    private String dynamoDbNameOffset;
    @Value("${dynamodb.table.name}")
    private String dynamoDbName;
    @Value("${dynamodb.table.secondary.index}")
    private String dynamoDbSecondaryIndex;

    @Value("${aws.access.key:#{null}}")
    private String awsAccessKey;
    @Value("${aws.secret.key:#{null}}")
    private String awsSecretKey;
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${run.scheduler}")
    private boolean isRunScheduler;
    @Value("${run.api}")
    private boolean isRunApi;

}
