package com.animeshagrawal.timerservice.configs;

import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.constants.QuartzJobName;
import com.animeshagrawal.timerservice.dbActions.DynamoDbConnector;
import com.animeshagrawal.timerservice.dbActions.SqsConnector;
import com.animeshagrawal.timerservice.model.Offset;
import com.animeshagrawal.timerservice.tasks.QuartzScheduler;
import com.animeshagrawal.timerservice.tasks.SqsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.animeshagrawal.timerservice.constants.Constants.SQS_CONSUMERS;
import static com.animeshagrawal.timerservice.constants.Constants.PARTITIONS;
import static com.animeshagrawal.timerservice.model.Offset.getOffset;


@Configuration
@Slf4j
public class BeanInitializer {

    @Autowired
    private AppConfigs appConfigs;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("Initializing Constants, start threads and making connections");
        Constants.OFFSET_DYNAMO_NAME = appConfigs.getDynamoDbNameOffset();
        Constants.INTERNAL_SQS_QUEUE_NAME = appConfigs.getInternalQueueName();
        Constants.INTERNAL_SQS_QUEUE_URL = appConfigs.getInternalQueueUrl();
        Constants.DYNAMODB_NAME = appConfigs.getDynamoDbName();
        Constants.AWS_REGION = appConfigs.getAwsRegion();
        Constants.DYNAMODB_SECONDARY_INDEX = appConfigs.getDynamoDbSecondaryIndex();
        Constants.AWS_ACCESS_KEY = appConfigs.getAwsAccessKey();
        Constants.AWS_SECRET_KEY = appConfigs.getAwsSecretKey();
        Constants.KAFKA_BROKER = appConfigs.getKafkaServer();
        Constants.KAFKA_SCHEMA_URL = appConfigs.getKafkaSchemaUrl();
        SqsConnector s;
        DynamoDbConnector d;
        this.startProducers(appConfigs);
        if (Constants.AWS_SECRET_KEY != null && Constants.AWS_ACCESS_KEY != null) {
            s = new SqsConnector(Constants.AWS_ACCESS_KEY, Constants.AWS_SECRET_KEY, Constants.AWS_REGION);
            d = new DynamoDbConnector(Constants.AWS_ACCESS_KEY, Constants.AWS_SECRET_KEY, Constants.AWS_REGION);
        } else {
            s = new SqsConnector(Constants.AWS_REGION);
            d = new DynamoDbConnector(Constants.AWS_REGION);
        }
        Constants.SQS = SqsConnector.sqsClient;
        Constants.DYNAMODB = DynamoDbConnector.dynamoDB;
        Constants.DYNAMODB_CLIENT = DynamoDbConnector.client;
        this.setInitOffset();
        if (appConfigs.isRunScheduler()) {
            if (this.startScheduler()) {
                log.info("Scheduler Started!!");
            } else {
                throw new Exception("Could not start scheduler, application shutting down!");
            }
        } else if (appConfigs.isRunApi()) {
            log.info("Api Started!!");
        }
        else {
            log.info("SQS Consumer Started!!");
            ExecutorService executor = Executors.newFixedThreadPool(SQS_CONSUMERS);
            for (int i = 0; i < SQS_CONSUMERS; i++) {
                SqsConsumer sqsConsumer = new SqsConsumer(appConfigs);
                executor.submit(sqsConsumer);
            }
        }
    }


    private boolean startScheduler() {
        try {
            QuartzScheduler quartzScheduler = new QuartzScheduler();

            quartzScheduler.scheduleTask(QuartzJobName.TIMER_EVENTS.getJobName(),
                    "TIMER_EVENTS_TRIGGER",
                    "TIMER_EVENTS_JOB_GROUP"
            );

        } catch (Exception e) {
            log.error("Error while starting schedulers " + e);
            return false;
        }
        return true;
    }

    private void setInitOffset() throws Exception {
        for(int i = 0; i < PARTITIONS; i ++) {
            Offset offset = getOffset(i);
            if (offset == null) {
                log.info("First start-up");
                offset = new Offset();
                offset.setPartitionId(i);
                offset.setCurrentOffset(0);
                offset.setEndOffset(0);
                offset.setMaxEpoch(0);
                offset.setOffset();
            } else {
                offset.setLag();
                offset.setOffset();
                log.info("Offset are initialized!");
            }
        }
    }

    private boolean startProducers(AppConfigs appConfigs){
        Properties props = new Properties();
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfigs.getKafkaServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put("schema.registry.url", appConfigs.getKafkaSchemaUrl());
        props.put("linger.ms",10);
        Constants.AVRO_PRODUCER = new KafkaProducer<Object, Object>(props);
        return true;
    }
}
