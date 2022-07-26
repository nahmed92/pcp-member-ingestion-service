package com.delta.pcpingestion.service;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.repo.IngestionControllerRepository;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing PCP Member Ingestion Service Impl")
@Configuration
@Slf4j
public class IngestionServiceTest {

	@Mock
	private IngestionControllerRepository repo;

	@Mock
	private TibcoClient tibcoClient;

	@Mock
	private ContractIngester contractIngester;
	
	@Mock
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	@InjectMocks
	private IngestionService ingestionService;
	
	@Test
	public void testIngestByState() throws Exception {
    	ReflectionTestUtils.setField(ingestionService, "serviceInstanceId", "Test");
		ContractEntity entity = getContractEntity();
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.now().toString());		
				
		when(tibcoClient.fetchContracts(param)).thenReturn(Lists.list(entity));
		doReturn(Optional.of(getIngestionControllerEntity()),Optional.empty()).when(repo).findFirstByStatusAndServiceInstanceId(ControlStatus.CREATED,"Test");
		ingestionService.ingest();
		Mockito.verify(repo, atLeastOnce()).save(ArgumentMatchers.any());
		
	}
	
	@Test
	public void testIngestInProgress() throws Exception {
    	ReflectionTestUtils.setField(ingestionService, "serviceInstanceId", "Test");
		ContractEntity entity = getContractEntity();
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.now().toString());		
				
		when(tibcoClient.fetchContracts(param)).thenReturn(Lists.list(entity));
		doReturn(List.of(getIngestionControllerEntity())).when(repo).findAllByStatusAndServiceInstanceId(ControlStatus.IN_PROGRESS,"Test");
		ingestionService.ingestInProgress();
		Mockito.verify(repo, atLeastOnce()).save(ArgumentMatchers.any());
		
	}


   
    private IngestionControllerEntity getIngestionControllerEntity() {
    return IngestionControllerEntity.builder().id("123")
			.runId("123")
			.serviceInstanceId("localhost")
			.status(ControlStatus.CREATED)
			.states("CA,NY")
			.build();
    }
    
    private ContractEntity getContractEntity() {
    	return ContractEntity.builder()
    			.claimIds("20220706040584")
    			.contractId("1192845828")
    			.id(UUID.randomUUID().toString())
    			.contractJson("{\r\n"
    					+ "   \"contractID\":\"1192845828\",\r\n"
    					+ "   \"groupNumber\":null,\r\n"
    					+ "   \"divisionNumber\":null,\r\n"
    					+ "   \"enrollees\":[\r\n"
    					+ "      {\r\n"
    					+ "         \"memberId\":\"02\",\r\n"
    					+ "         \"networkId\":null,\r\n"
    					+ "         \"providerID\":null,\r\n"
    					+ "         \"product\":null,\r\n"
    					+ "         \"mtvPersonID\":\"0645582313463175\",\r\n"
    					+ "         \"memberAddress\":null,\r\n"
    					+ "         \"claims\":[\r\n"
    					+ "            {\r\n"
    					+ "               \"claimId\":\"20220706040584\",\r\n"
    					+ "               \"billingProviderId\":\"PRV240845172\",\r\n"
    					+ "               \"billProviderSpeciality\":null,\r\n"
    					+ "               \"receivedDate\":\"11-Mar-22\",\r\n"
    					+ "               \"resolvedDate\":\"2022-03-21 12:47:48\",\r\n"
    					+ "               \"serviceNumber\":null,\r\n"
    					+ "               \"emergencyFlag\":null,\r\n"
    					+ "               \"encounterFlag\":null,\r\n"
    					+ "               \"claimStatus\":\"Y\",\r\n"
    					+ "               \"stateCode\":\"CT\",\r\n"
    					+ "               \"securityGroupId\":\"79202\"\r\n"
    					+ "            }\r\n"
    					+ "         ]\r\n"
    					+ "      }\r\n"
    					+ "   ]\r\n"
    					+ "}")
    			.build();
    }
	

}
