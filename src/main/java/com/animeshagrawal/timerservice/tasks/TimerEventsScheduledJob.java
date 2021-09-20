package com.animeshagrawal.timerservice.tasks;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class TimerEventsScheduledJob implements Job {

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerContext schedulerContext = null;
        schedulerContext = context.getScheduler().getContext();

        LocalDateTime localTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        log.info("Run ScheduleAdvisoryJob at IST: " + localTime.toString());
        TimerEventsProducer timerEventsProducer = new TimerEventsProducer(context.getScheduledFireTime(), (Integer) schedulerContext.get("partition_id"));

        timerEventsProducer.init();
    }
}
