package com.delta.pcpingestion.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delta.pcpingestion.service.IngestionControllerService;
import com.delta.pcpingestion.service.IngestionService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Member Ingestion Controller Test")
@Configuration
@Slf4j
public class MemberIngestionControllerTest {
	
	@Mock
	private IngestionControllerService service;
	
	@Mock
	private IngestionService ingestionService;
	
	@InjectMocks
	private MemberIngestionController memberIngestionController;
	
	@Test
	public void testPcpIngestion() throws Exception {
		ResponseEntity<Boolean> status = memberIngestionController.ingest();
		Assertions.assertEquals(status.getStatusCode(), HttpStatus.OK);
		
	}
	
	@Test
	public void testInitiateControll() throws Exception {
		ResponseEntity<Boolean> status = memberIngestionController.initiateControll();
		Assertions.assertEquals(status.getStatusCode(), HttpStatus.OK);
		
	}
}
