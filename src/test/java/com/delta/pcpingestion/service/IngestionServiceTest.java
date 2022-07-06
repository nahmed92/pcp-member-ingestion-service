package com.delta.pcpingestion.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.repo.ContractDAO;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
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

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.repo.ContractRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing PCP Member Ingestion Service Impl")
@Configuration
@Slf4j
public class IngestionServiceTest {

	@Mock
	private ContractDAO repository;

	@Mock
	private TibcoClient tibcoClient;

	@Mock
	private com.delta.pcpingestion.interservice.PCPConfigServiceClient configClient;

	@InjectMocks
	private ContractIngester contractIngester;


	@Test
	public void testIngestByState() throws Exception {
		ContractEntity entity = ContractEntity.builder() //
				.claimIds("1234").contractId("3456").id(UUID.randomUUID().toString()).build();
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.now().toString());
		
				
		when(tibcoClient.fetchContracts(param)).thenReturn(Lists.list(entity));
		LocalDate cutOffDays  = LocalDate.now().minusDays(5);
		IngestionControllerEntity controllEntity = IngestionControllerEntity.builder()
				.states("CA")
				.cutOffDate(Date.valueOf( cutOffDays))
				.noOfDays(5)
				.build();
		contractIngester.ingest(controllEntity);
		Mockito.verify(tibcoClient, times(1)).fetchContracts(ArgumentMatchers.any(Map.class));
		
	}
	

}
