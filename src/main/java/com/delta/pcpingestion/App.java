package com.delta.pcpingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class App {

	public static void main(String[] args) {
		log.info("Starting PCP Member Ingestion Service ");
		SpringApplication.run(App.class, args);
	}

}