package com.delta.pcpingestion.scheduler;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.service.IngestionControllerService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing Mapper")
@Configuration
@Slf4j
public class IngestionControllerSchedulerTest {

	@Mock
	private IngestionControllerService service;
	
	@InjectMocks
	private IngestionControllerScheduler schedular;
	
	@Test
	public void testSchedule() {
		ReflectionTestUtils.setField(schedular, "controllerAllowedInstanceId", "testSchedular");	
		ReflectionTestUtils.setField(schedular, "serviceInstanceId", "testSchedular");
		ReflectionTestUtils.setField(schedular, "enableIngestionController", Boolean.TRUE);
		schedular.schedule();
		verify(service, atLeastOnce()).populateControl();
	}
	
	@Test
	public void testScheduleWhEnenableIngestionControllerFalse() {
		ReflectionTestUtils.setField(schedular, "controllerAllowedInstanceId", "testSchedular");	
		ReflectionTestUtils.setField(schedular, "serviceInstanceId", "testSchedular");
		ReflectionTestUtils.setField(schedular, "enableIngestionController", Boolean.FALSE);
		schedular.schedule();
		verify(service, times(0)).populateControl();
	}
}
