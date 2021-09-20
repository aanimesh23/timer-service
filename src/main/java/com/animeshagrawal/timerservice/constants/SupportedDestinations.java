package com.animeshagrawal.timerservice.constants;

import lombok.Getter;

@Getter
public enum SupportedDestinations {
    SQS("SQS"),
    KAFKA("KAFKA"),
    API("API");

    private String name;

    SupportedDestinations(String name) {
        this.name = name;
    }
    public static boolean containsDestination(String name) {
        SupportedDestinations destinations[] = SupportedDestinations.values();
        for(SupportedDestinations destination : destinations){
            if(destination.getName().equalsIgnoreCase(name))return true;
        }
        return false;
    }
}
