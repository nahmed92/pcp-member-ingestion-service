package com.delta.pcpingestion.service;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.mapper.Mapper;
import com.delta.pcpingestion.repo.ContractDAO;
import com.delta.pcpingestion.repo.IngestionStatsRepository;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Contract Ingester Service Test")
public class ContractIngesterTest {
	
	@Mock
	private TibcoClient tibcoClient;

	@Mock
	ContractDAO contractDAO;

	@Mock
	private IngestionStatsRepository statsRepo;
	
	@Mock
	private Mapper mapper;
	
	
	@InjectMocks
	private ContractIngester contractIngester;
	
	@Test
	public void testIngest() throws Exception{
	ReflectionTestUtils.setField(contractIngester, "serviceInstanceId", "test-inst");
	ContractEntity contractEntity = getContractEntity();
	DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	Map<String, String> param = new HashMap<>();
	param.put("state", "CA");
	param.put("numofdays", "5");
	param.put("receiveddate", LocalDate.now().format(df));
	param.put("pagenum", "0");
	
	doReturn(List.of(contractEntity)).when(tibcoClient).fetchContracts(param);
	LocalDate cutOffDays  = LocalDate.now().minusDays(2);
	IngestionControllerEntity controllEntity = IngestionControllerEntity.builder()
			.states("CA")
			.cutOffDate(Date.valueOf( cutOffDays))
			.noOfContracts(1)
			.noOfDays(5)
			.build();
	contractIngester.ingest(controllEntity);
	Mockito.verify(tibcoClient, atLeastOnce()).fetchContracts(ArgumentMatchers.anyMap());	
	}
	
	@Test
	public void testIngestWhenNoRecordFromTibco() throws Exception{
	ReflectionTestUtils.setField(contractIngester, "serviceInstanceId", "test-inst");
	DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	Map<String, String> param = new HashMap<>();
	param.put("state", "CA");
	param.put("numofdays", "5");
	param.put("receiveddate", LocalDate.now().format(df));
	param.put("pagenum", "0");
	
	doReturn(List.of()).when(tibcoClient).fetchContracts(param);
	LocalDate cutOffDays  = LocalDate.now().minusDays(2);
	IngestionControllerEntity controllEntity = IngestionControllerEntity.builder()
			.states("CA")
			.cutOffDate(Date.valueOf( cutOffDays))
			.noOfContracts(1)
			.noOfDays(5)
			.build();
	contractIngester.ingest(controllEntity);
	Mockito.verify(tibcoClient, atLeastOnce()).fetchContracts(ArgumentMatchers.anyMap());	
	}
	
	
	@Test
	public void testSaveAllreadyExistContract() {
        doReturn(Optional.of(getContractEntity())).when(contractDAO).findByContractId("1192845828");
		contractIngester.save(getContractEntity());
		Mockito.verify(contractDAO, atLeastOnce()).save(ArgumentMatchers.any());
	}
	
	@Test
	public void testSave() {
        doReturn(Optional.empty()).when(contractDAO).findByContractId("1192845828");
		contractIngester.save(getContractEntity());
		Mockito.verify(contractDAO, atLeastOnce()).save(ArgumentMatchers.any());
	}
	
    private ContractEntity getContractEntity() {
    	return ContractEntity.builder()
    			.claimIds("20220706040584")
    			.contractId("1192845828")
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
