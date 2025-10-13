package com.example.monitoreo.monitoreo_gpon_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitoreoGponBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoreoGponBackApplication.class, args);
	}

}
