package com.delta.pcpingestion.service;

import static org.mockito.Mockito.doReturn;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.interservice.PCPConfigServiceClient;
import com.delta.pcpingestion.repo.IngestionControllerRepository;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Ingestion Controller Service Test")
public class IngestionControllerServiceTest {
	
	@Mock
	private IngestionControllerRepository repo;

	@Mock
	private PCPConfigServiceClient configClient;
	
	@InjectMocks
	private IngestionControllerService ingestionControllerService;
	
	
	@Test
	public void testPopulateControl() throws Exception{
		ReflectionTestUtils.setField(ingestionControllerService, "numOfDays", 10);
		ReflectionTestUtils.setField(ingestionControllerService, "serviceNodes", "testNode");
		String[] serviceInstances = {"testInstance"};
		ReflectionTestUtils.setField(ingestionControllerService, "serviceInstances", serviceInstances);
		doReturn("99").when(configClient).claimLookBackDays();
		ingestionControllerService.populateControl();
		
	    Mockito.verify(repo, times(1)).saveAll(Mockito.anyList());
		
	}

}
