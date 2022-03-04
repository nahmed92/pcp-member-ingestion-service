package com.delta.pcpingestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.dto.Claim;
import com.delta.pcpingestion.dto.Contract;
import com.delta.pcpingestion.dto.Enrollee;
import com.delta.pcpingestion.dto.Member;
import com.delta.pcpingestion.dto.MemberAddress;
import com.delta.pcpingestion.dto.PcpMember;
import com.delta.pcpingestion.entity.PCPMemberContract;
import com.delta.pcpingestion.repo.ContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing PCP Member Ingestion Service Impl")
@Slf4j
public class PCPIngestionServiceTest {

	@Mock
	private ContractRepository repository;

	@Mock
	private TibcoClient tibcoRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

	@InjectMocks
	private PCPIngestionService pcpIngestionService;

	private PcpMember pcpMember;

	@BeforeEach
	public void before() {
	ReflectionTestUtils.setField(pcpIngestionService, "objectMapper", objectMapper);	
		pcpMember = new PcpMember();
		Claim claim = new Claim();
		claim.setClaimId("1");
		claim.setBillingProviderId("GRP782411662");
		claim.setBillProviderSpeciality("GRP782411661");
		claim.setReceivedDate("2017-09-28 00:00:01.000000");

		Claim claim1 = new Claim();
		claim1.setClaimId("12");
		claim1.setBillingProviderId("GRP782411661");
		claim1.setBillProviderSpeciality("GRP782411661");
		claim1.setReceivedDate("2017-09-28 00:00:01.000000");

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
	
	@Test
	public void testGetAllContract() throws Exception {
		Set<String> ids = new HashSet<>();
		ids.add("1234");
		ids.add("432");
		PCPMemberContract entity = PCPMemberContract.builder()
				.contract("{\"contractID\":\"1209709016\",\"groupNumber\":null,\"divisionNumber\":null,\"enrollees\":[{\"memberId\":\"02\",\"networkId\":null,\"providerID\":null,\"product\":null,\"mtvPersonID\":\"0198113012305856\",\"memberAddress\":null,\"claims\":[{\"claimId\":\"20220186124792\",\"billingProviderId\":\"PRV240829640\",\"billProviderSpeciality\":null,\"receivedDate\":\"2022-01-18 00:00:00.0\",\"resolvedDate\":\"2022-01-21 00:00:00.0\",\"serviceNumber\":null,\"emergencyFlag\":null,\"encounterFlag\":null}]}]}")
				.contractID("123")
				.memberId(ids)
				.mtvPersonID(ids)
				.status(STATUS.STAGED)
				.numberOfEnrollee(3)
				.numOfAttempt(0)
				.build();
		List<PCPMemberContract> list = new ArrayList<>();
		list.add(entity);
		when(repository.findAll()).thenReturn(list);
		List<PCPMemberContract> response = pcpIngestionService.getAllContract();
		assertEquals(response.size(), 1);
		assertEquals(response.get(0).getContractID(), "123");
		assertEquals(response.get(0).getMemberId().size(), 2);
		assertEquals(response.get(0).getMtvPersonID().size(), 2);
		assertEquals(response.get(0).getStatus(), STATUS.STAGED);
   }

	@Test
	public void testCreateNewPCPMembercontract() throws Exception {
		Set<String> ids = new HashSet<>();
		ids.add("1234");
		ids.add("432");
		PCPMemberContract entity = PCPMemberContract.builder()
				.contract("{\"contractID\":\"1209709016\",\"groupNumber\":null,\"divisionNumber\":null,\"enrollees\":[{\"memberId\":\"02\",\"networkId\":null,\"providerID\":null,\"product\":null,\"mtvPersonID\":\"0198113012305856\",\"memberAddress\":null,\"claims\":[{\"claimId\":\"20220186124792\",\"billingProviderId\":\"PRV240829640\",\"billProviderSpeciality\":null,\"receivedDate\":\"2022-01-18 00:00:00.0\",\"resolvedDate\":\"2022-01-21 00:00:00.0\",\"serviceNumber\":null,\"emergencyFlag\":null,\"encounterFlag\":null}]}]}")
				.contractID("123")
				.memberId(ids)
				.mtvPersonID(ids)
				.status(STATUS.STAGED)
				.numberOfEnrollee(3)
				.numOfAttempt(0)
				.build();
		List<PCPMemberContract> list = new ArrayList<>();
		list.add(entity);
		String tibcoQuery = "{'pcpMembersRequest':'{\"states\":[\"NC\",\"OK\",\"AK\",\"CT\",\"LA\"],\"numofdays\":30,\"receiveddate\":\"16-FEB-22 12:00:00 AM\",\"pagenum\":${pagenum}}'}";
		Member member = new Member();
		member.setPcpMembers(pcpMember);
		ResponseEntity<Member> pcpMemberResp = new ResponseEntity<Member>(member, HttpStatus.OK);
		when(tibcoRestTemplate
				.fetchPcpmemberFromTibco( "{'pcpMembersRequest':'{\"states\":[\"NC\",\"OK\",\"AK\",\"CT\",\"LA\"],\"numofdays\":30,\"receiveddate\":\"16-FEB-22 12:00:00 AM\",\"pagenum\":0}'}"))
						.thenReturn(pcpMemberResp);
		pcpIngestionService.createPCPContract(tibcoQuery);

	}
	
}
