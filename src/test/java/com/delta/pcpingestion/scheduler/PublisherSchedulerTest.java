package com.delta.pcpingestion.scheduler;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delta.pcpingestion.service.PublisherService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing Mapper")
@Configuration
@Slf4j
public class PublisherSchedulerTest {
	
	@Mock
	private PublisherService publisherService;
	
	@InjectMocks
	private  PublisherScheduler schedular;
	
	@Test
	public void testPublic() {
		schedular.publish();
		verify(publisherService, atLeastOnce()).publish();
		
	}

}
