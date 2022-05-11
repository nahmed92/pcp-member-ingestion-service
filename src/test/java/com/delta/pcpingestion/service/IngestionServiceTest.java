package com.delta.pcpingestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.interservice.tibco.dto.Claim;
import com.delta.pcpingestion.interservice.tibco.dto.Contract;
import com.delta.pcpingestion.interservice.tibco.dto.Enrollee;
import com.delta.pcpingestion.interservice.tibco.dto.Member;
import com.delta.pcpingestion.interservice.tibco.dto.MemberAddress;
import com.delta.pcpingestion.interservice.tibco.dto.PcpMember;
import com.delta.pcpingestion.repo.ContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing PCP Member Ingestion Service Impl")
@Configuration
@Slf4j
public class IngestionServiceTest {

	@Mock
	private ContractRepository repository;

	@Mock
	private TibcoClient tibcoRestTemplate;

	@Mock
	private com.delta.pcpingestion.interservice.PCPConfigServiceClient configClient;

	@Autowired
	private ObjectMapper objectMapper;

	@InjectMocks
	private IngestionService pcpIngestionService;

	private PcpMember pcpMember;

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(pcpIngestionService, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(pcpIngestionService, "pcpIngestionProcessWorkersCount", 8);
		pcpMember = new PcpMember();
		Claim claim = new Claim();
		claim.setClaimId("1");
		claim.setBillingProviderId("GRP782411662");
		claim.setBillProviderSpeciality("GRP782411661");
		// claim.setReceivedDate(new Date("2017-09-28 00:00:01.000000"));

		Claim claim1 = new Claim();
		claim1.setClaimId("12");
		claim1.setBillingProviderId("GRP782411661");
		claim1.setBillProviderSpeciality("GRP782411661");
		// claim1.setReceivedDate(LocalDate.from("2017-09-28 00:00:01.000000"));

		MemberAddress memberAddress = new MemberAddress();
		memberAddress.setAddressLine1("La Mirag");
		memberAddress.setCity("Albuquerque");
		memberAddress.setState("NM");

		Enrollee enrollee = new Enrollee();
		enrollee.setClaims(Lists.newArrayList(claim, claim1));
		enrollee.setMemberAddress(memberAddress);
		enrollee.setMemberId("123");

		Contract contract = new Contract();
		contract.setContractID("123");
		contract.setDivisionNumber("1234");
		contract.setGroupNumber("1234");
		contract.setEnrollees(Lists.newArrayList(enrollee));

		pcpMember.setContracts(Lists.newArrayList(contract));

	}

//	@Test
//	public void testCreateNewPCPMembercontract() throws Exception {
//		String ids = "1234,432";
//		ContractEntity entity = ContractEntity.builder().contractJson(
//				"{\"contractID\":\"1209709016\",\"groupNumber\":null,\"divisionNumber\":null,\"enrollees\":[{\"memberId\":\"02\",\"networkId\":null,\"providerID\":null,\"product\":null,\"mtvPersonID\":\"0198113012305856\",\"memberAddress\":null,\"claims\":[{\"claimId\":\"20220186124792\",\"billingProviderId\":\"PRV240829640\",\"billProviderSpeciality\":null,\"receivedDate\":\"2022-01-18 00:00:00.0\",\"resolvedDate\":\"2022-01-21 00:00:00.0\",\"serviceNumber\":null,\"emergencyFlag\":null,\"encounterFlag\":null}]}]}")
//				.contractId("123").mtvPersonIds(ids).publishStatus(PublishStatus.STAGED).numOfRetries(3)
//				.numOfRetries(0).build();
//		List<ContractEntity> list = new ArrayList<>();
//		list.add(entity);
//		String tibcoQuery = "{'pcpMembersRequest':'{\"states\":[\"NC\",\"OK\",\"AK\",\"CT\",\"LA\"],\"numofdays\":30,\"receiveddate\":\"16-FEB-22 12:00:00 AM\",\"pagenum\":${pagenum}}'}";
//		Member member = new Member();
//		member.setPcpMembers(pcpMember);
//		ResponseEntity<Member> pcpMemberResp = new ResponseEntity<Member>(member, HttpStatus.OK);
//		when(tibcoRestTemplate.fetchContracts(
//				"{'pcpMembersRequest':'{\"states\":[\"NC\",\"OK\",\"AK\",\"CT\",\"LA\"],\"numofdays\":30,\"receiveddate\":\"16-FEB-22 12:00:00 AM\",\"pagenum\":0}'}"))
//						.thenReturn(pcpMemberResp);
//		when(configClient.providerLookBackDays()).thenReturn("60");
//		pcpIngestionService.createPCPContract();
//
//	}

}
