package com.delta.pcpingestion.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import com.delta.pcpingestion.entity.PCPMemberContractEntity;
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
	private ContractRepository repo;

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
//	ReflectionTestUtils.setField(pcpIngestionService, "basicAuthUser", "mockUser");
//	ReflectionTestUtils.setField(pcpIngestionService, "basicAuthPassword", "mockPassword");
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
	public void shouldCreateNewPCPMembercontract() throws Exception {
		String tibcoQuery = "{'pcpMembersRequest':'{\"states\":[\"NC\",\"OK\",\"AK\",\"CT\",\"LA\"],\"numofdays\":30,\"receiveddate\":\"16-FEB-22 12:00:00 AM\",\"pagenum\":0}'}";
		Member member = new Member();
		member.setPcpMembers(pcpMember);
		ResponseEntity<Member> pcpMemberResp = new ResponseEntity<Member>(member, HttpStatus.OK);
		when(tibcoRestTemplate
				.fetchPcpmemberFromTibco(tibcoQuery))
						.thenReturn(pcpMemberResp);
		pcpIngestionService.createPCPContract(tibcoQuery);
		List<PCPMemberContractEntity> contract = repo.findAll();
		log.info("contract is["+contract+"]");
		verify(repo, times(1));

	}
}
