package com.animeshagrawal.timerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class TimerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimerServiceApplication.class, args);
	}

	@PreDestroy
	public void onShutDown() {
		System.out.println("closing application context..let's do the final resource cleanup");
	}

}
