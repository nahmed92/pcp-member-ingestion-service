package com.delta.pcpingestion.scheduler;

import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delta.pcpingestion.scheduler.IngestionScheduler;
import com.delta.pcpingestion.service.IngestionService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Ingestion Scheduler Test")
@Configuration
@Slf4j
public class IngestionSchedulerTest {
	
	@Mock
	private IngestionService ingestionService;
	
	@InjectMocks
	private IngestionScheduler ingestionScheduler;
	
	@Test
	public void testIngest() {
		ingestionScheduler.ingest();
		Mockito.verify(ingestionService, times(1)).ingest();
	}
}
