package com.delta.pcpingestion.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.concurrent.Executors;
import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PcpCalculationServiceClient;
import com.delta.pcpingestion.repo.ContractRepository;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing Publish Contract Service Impl")
@Configuration
@Slf4j
public class PublisherServiceTest {
	
	@Mock
	private ContractRepository repository;
	
	@Mock
	private PcpCalculationServiceClient pcpCalculationClient;
	
	@Mock
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	@InjectMocks
	private PublisherService publishContract;
	
	@Test
	public void testPublish() throws Exception {
		ContractEntity entity = ContractEntity.builder() //
				.claimIds("1234").contractId("3456").id(UUID.randomUUID().toString()).build();
		when(repository.findByPublishStatusAndStateCode(PublishStatus.STAGED.name(), "CA")).thenReturn(Lists.list(entity));
		publishContract.publish();		
	}
	
	
	
	

}
