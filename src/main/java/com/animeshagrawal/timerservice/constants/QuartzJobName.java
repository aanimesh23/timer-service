package com.animeshagrawal.timerservice.constants;

import lombok.Getter;

@Getter
public enum QuartzJobName {
    TIMER_EVENTS("TIMER_EVENTS");

    private String jobName;
    QuartzJobName(String jobName){
        this.jobName = jobName;
    }
}
