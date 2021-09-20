# Timer-Service

A means of receiving your events at a configurable delay

## Description

Timer service is a service that can be used in applications to get a message back to you at a particular time. These callbacks can be either in the form of an API callback or can also be an event produced in SQS or Kafka. An event is anything you want this service to send back to you, it could be a string message, a flag, or event an entire payload.

## Blog
[@Blog](https://medium.com/@aanimesh23/how-to-schedule-deliveries-of-events-with-a-configurable-time-98060e233238)

## Getting Started

### Dependencies

* Java 11
* AWS DynamoDB
* AWS SQS

### Installing

* Fill in all the blank values in application.properties file in resources
* Make sure you have Java 11 downloaded

### Executing program

* Run the command to build the project
```
./mvnw clean install
```
* Run Scheduler
```
sudo java -jar target/timer-service-1.0.1.jar --run.scheduler=true --spring.main.web-application-type=NONE
```
* Run API
```
sudo java -jar target/timer-service-1.0.1.jar --run.api=true
```
* Run Workers
```
sudo java -jar target/timer-service-1.0.1.jar --spring.main.web-application-type=NONE
```

## Authors

Animesh Agrawal
[@aanimesh23](http://animeshagrawal.com)

## Acknowledgments

Inspiration and Advisors.
* Sunny Shah
* Rahul Sharma