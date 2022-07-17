package com.delta.pcpingestion.mapper;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.delta.pcpingestion.interservice.tibco.dto.Claim;
import com.delta.pcpingestion.interservice.tibco.dto.Contract;
import com.delta.pcpingestion.interservice.tibco.dto.Enrollee;
import com.deltadental.platform.common.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing Mapper")
@Configuration
@Slf4j
public class MapperTest {
	
	@InjectMocks
	private Mapper mapper;
	
	private ObjectMapper objectMapper;
	
	 @BeforeAll
	 public void setUp() throws Exception {
		 objectMapper = new ObjectMapper();
	 }
	
	private String contractJson = "{"
			+ "   \"contractId\":\"1192845828\","
			+ "   \"groupNumber\":null,"
			+ "   \"divisionNumber\":null,"
			+ "   \"enrollees\":["
			+ "      {"
			+ "         \"memberId\":\"02\","
			+ "         \"networkId\":null,"
			+ "         \"providerID\":null,"
			+ "         \"product\":null,"
			+ "         \"mtvPersonId\":\"0645582313463175\","
			+ "         \"memberAddress\":null,"
			+ "         \"claims\":["
			+ "            {"
			+ "               \"claimId\":\"20220706040584\","
			+ "               \"billingProviderId\":\"DC\","
			+ "               \"billProviderSpeciality\":null,"
			+ "               \"serviceNumber\":null,"
			+ "               \"emergencyFlag\":null,"
			+ "               \"encounterFlag\":null,"
			+ "               \"claimStatus\":\"Y\","
			+ "               \"stateCode\":\"CT\""
			+ "            }"
			+ "         ]"
			+ "      }"
			+ "   ]"
			+ "}";
	
	private String contractJsonWithMemberthree = "{"
			+ "   \"contractId\":\"1192845828\","
			+ "   \"groupNumber\":null,"
			+ "   \"divisionNumber\":null,"
			+ "   \"enrollees\":["
			+ "      {"
			+ "         \"memberId\":\"03\","
			+ "         \"networkId\":null,"
			+ "         \"providerID\":null,"
			+ "         \"product\":null,"
			+ "         \"mtvPersonId\":\"0645582313463175\","
			+ "         \"memberAddress\":null,"
			+ "         \"claims\":["
			+ "            {"
			+ "               \"claimId\":\"40220706040592\","
			+ "               \"billingProviderId\":\"DC\","
			+ "               \"billProviderSpeciality\":null,"
			+ "               \"serviceNumber\":null,"
			+ "               \"emergencyFlag\":null,"
			+ "               \"encounterFlag\":null,"
			+ "               \"claimStatus\":\"Y\","
			+ "               \"stateCode\":\"CT\""
			+ "            }"
			+ "         ]"
			+ "      }"
			+ "   ]"
			+ "}";
	
	
	private String contractJsonBillngProviderNotDc = "{"
			+ "   \"contractId\":\"1192845828\","
			+ "   \"groupNumber\":null,"
			+ "   \"divisionNumber\":null,"
			+ "   \"enrollees\":["
			+ "      {"
			+ "         \"memberId\":\"02\","
			+ "         \"networkId\":null,"
			+ "         \"providerID\":null,"
			+ "         \"product\":null,"
			+ "         \"mtvPersonId\":\"0645582313463175\","
			+ "         \"memberAddress\":null,"
			+ "         \"claims\":["
			+ "            {"
			+ "               \"claimId\":\"20220706040584\","
			+ "               \"billingProviderId\":\"ABC\","
			+ "               \"billProviderSpeciality\":null,"
			+ "               \"serviceNumber\":null,"
			+ "               \"emergencyFlag\":null,"
			+ "               \"encounterFlag\":null,"
			+ "               \"claimStatus\":\"Y\","
			+ "               \"stateCode\":\"CT\""
			+ "            }"
			+ "         ]"
			+ "      }"
			+ "   ]"
			+ "}";
	
	private String contractJsonWithOutEnrolle = "{"
			+ "   \"contractId\":\"1192845828\","
			+ "   \"groupNumber\":null,"
			+ "   \"divisionNumber\":null,"
			+ "   \"enrollees\":["
			+ "   ]"
			+ "}";
	
	private String contractJsonClaimIsEmpty = "{"
			+ "   \"contractId\":\"1192845828\","
			+ "   \"groupNumber\":null,"
			+ "   \"divisionNumber\":null,"
			+ "   \"enrollees\":["
			+ "      {"
			+ "         \"memberId\":\"02\","
			+ "         \"networkId\":null,"
			+ "         \"providerID\":null,"
			+ "         \"product\":null,"
			+ "         \"mtvPersonId\":\"0645582313463175\","
			+ "         \"memberAddress\":null,"
			+ "         \"claims\":["
			+ "         ]"
			+ "      }"
			+ "   ]"
			+ "}";
	@Test
	public void testMap() {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		List<ContractEntity> entities = mapper.map(List.of(getMemberContract()));
		Assertions.assertEquals(entities.size(),1);
		Assertions.assertEquals(entities.get(0).getContractId(), "1192845828");
	}
	
//	@Test
//	public void testMapException() {
//		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
//		Contract contract = getMemberContract();
//		contract.setContractId("1112_ACV-3#%%',");
//		mapper.map(contract);
//	}
	
	@Test
	public void testMerge() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
	    ContractEntity entity = mapper.merge(getContractEntity(), getContractEntity());
		Assertions.assertNotNull(entity);

	}
	
	@Test
	public void testMergeWhenEnrolleeDiffrentMember() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		ContractEntity entity = getContractEntity();
		entity.setContractJson(contractJsonWithMemberthree);
	    ContractEntity contractEntity = mapper.merge(entity, getContractEntity());
		Assertions.assertNotNull(contractEntity);

	}
	
	@Test
	public void testMergeWhenEnrolleeEmpty() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		ContractEntity entity = getContractEntity();
		entity.setContractJson(contractJsonWithOutEnrolle);
	    ContractEntity contractEntity = mapper.merge(entity, getContractEntity());
		Assertions.assertNotNull(contractEntity);

	}
	
	@Test
	public void testMergeWhenClaimEmpty() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		ContractEntity entity = getContractEntity();
		entity.setContractJson(contractJsonClaimIsEmpty);
	    ContractEntity contractEntity = mapper.merge(entity, getContractEntity());
		Assertions.assertNotNull(contractEntity);

	}
	
	@Test
	public void testConvertStringToContract() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		try {
	      mapper.convertToContract("");
		} catch(ServiceException exception) {
//			Assertions.assertEquals(exception.getErrorCode().toString() ,
//					PCPMemberIngestionErrors.INTERNAL_SERVER_ERROR.name());
		}	

	}
	
	@Test
	public void testMergeWhenEnrolleeIsEmpty() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
	    ContractEntity entity = mapper.merge(getContractEntity(), getContractEntity());
		Assertions.assertNotNull(entity);

	}
	
	@Test
	public void testMapRequest() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		List<MemberContractClaimRequest> contract = mapper.mapRequest(getContractEntity());
		Assertions.assertNotNull(contract);

	}
	
	@Test
	public void testMapRequestNotDC() throws Exception {
		ReflectionTestUtils.setField(mapper, "objectMapper", objectMapper,  ObjectMapper.class);
		ContractEntity entity = getContractEntity();
		entity.setContractJson(contractJsonBillngProviderNotDc);
		List<MemberContractClaimRequest> contract = mapper.mapRequest(entity);
		Assertions.assertNotNull(contract);

	}
	
	private Contract getMemberContract() {
		Contract memberContract = new Contract();
		memberContract.setContractId("1192845828");
		memberContract.setDivisionNumber("11200");
		memberContract.setGroupNumber("2");
		Enrollee enrollee = new Enrollee();
		Claim claim =new Claim();
		claim.setClaimId("20220706040584");
		claim.setBillingProviderId("bill");
		claim.setClaimStatus("Active");
		claim.setGroupNumber("group1");	;
		enrollee.setClaims(List.of(claim));
		memberContract.setEnrollees(List.of(enrollee));
		return memberContract;
	}
	
	
	 private ContractEntity getContractEntity() {
	    	return ContractEntity.builder()
	    			.claimIds("20220706040584")
	    			.contractId("1192845828")
	    			.id(UUID.randomUUID().toString())
	    			.contractJson(contractJson)
	    			.build();
	    }
		

}
