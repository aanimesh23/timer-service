package com.animeshagrawal.timerservice.tasks;

import com.animeshagrawal.timerservice.constants.Constants;
import com.animeshagrawal.timerservice.constants.QuartzJobName;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZoneId;
import java.util.Properties;
import java.util.TimeZone;


@Slf4j
public class QuartzScheduler {
    Scheduler scheduler;

    public QuartzScheduler() throws SchedulerException {
        Properties props = new Properties();
        props.setProperty("org.quartz.threadPool.threadCount", "10");
        scheduler = new StdSchedulerFactory(props).getScheduler();
        this.scheduler.start();
        log.info("QuartzScheduler started with no. of threads {} and metadata {}",
                scheduler.getMetaData().getThreadPoolSize(), scheduler.getMetaData());
    }

    public boolean scheduleTask(String jobName, String triggerName, String group) throws Exception {
        if(QuartzJobName.TIMER_EVENTS.getJobName().equalsIgnoreCase(jobName))
        {
            for (int i = 0; i < Constants.PARTITIONS; i++) {
                this.timerEventsScheduler(triggerName + "__partition__" + i,
                        jobName + "__partition__" + i, group + "__partition__" + i, i);
            }
            return true;
        }

        return false;
    }


    private void timerEventsScheduler(String triggerName, String jobName, String group, int partitionId) throws Exception {

        int INTERVAL_SECONDS = 60;
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, group)
                .withSchedule(this.getSimpleScheduler(INTERVAL_SECONDS))
                .build();
        JobDetail jobDetail = JobBuilder.newJob(TimerEventsScheduledJob.class).withIdentity(jobName, group).build();
        this.scheduler.getContext().put("partition_id", partitionId);
        this.scheduler.scheduleJob(jobDetail, trigger);
    }



    private SimpleScheduleBuilder getSimpleScheduler(int INTERVAL_SECONDS){
        return SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(INTERVAL_SECONDS).repeatForever();
    }

    private  CronScheduleBuilder getCronScheduler(String CRON_EXPRESSION) {
        return CronScheduleBuilder.cronSchedule(CRON_EXPRESSION).inTimeZone(
                TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata")));
    }

    public static void main(String[] args) throws Exception {
//        QuartzScheduler temp = new QuartzScheduler();
//        temp.scheduleTask("job", "trigger", "test");
//        System.exit(1);
    }
}
