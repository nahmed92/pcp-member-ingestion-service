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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delta.pcpingestion.service.IngestionService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Restart Ingestion Processor")
@Configuration
@Slf4j
public class RestartIngestionProcessorTest {

	@Mock
	private IngestionService ingestionService;
	
	@InjectMocks
	private RestartIngestionProcessor restartIngestionProcessor;
	
	@Test
	public void testIngestInProgress() {
		restartIngestionProcessor.ingestInProgress();
		Mockito.verify(ingestionService, times(1)).ingestInProgress();
	}
}
