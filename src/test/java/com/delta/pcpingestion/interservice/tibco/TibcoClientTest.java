package com.delta.pcpingestion.interservice.tibco;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.delta.pcpingestion.interservice.tibco.dto.Claim;
import com.delta.pcpingestion.interservice.tibco.dto.Contract;
import com.delta.pcpingestion.interservice.tibco.dto.Enrollee;
import com.delta.pcpingestion.interservice.tibco.dto.Member;
import com.delta.pcpingestion.interservice.tibco.dto.PcpMember;
import com.delta.pcpingestion.mapper.Mapper;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tibco Client Test")
public class TibcoClientTest {
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private Mapper mapper;
	
	@InjectMocks
	private TibcoClient tibcoClient;
	
	@Test
	public void testFetchContract() {
		ReflectionTestUtils.setField(tibcoClient, "tibcoPcpMemberUrl", "http://test.com", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthUser", "testUser", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthPassword", "testPass", String.class);
		ReflectionTestUtils.setField(tibcoClient, "tibcoQueryStr", "{'memberProviderAssignment':'{\"states\":[${state}],\"numberOfDays\":${numofdays},\"receivedDate\":\"${receiveddate}\",\"pageNumber\":${pagenum}}'}", String.class);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.parse("07-14-2022",df).toString());
		param.put("pagenum", "0");
		HttpEntity<?> entity = new HttpEntity<>("{'memberProviderAssignment':'{\"states\":[CA],\"numberOfDays\":5,\"receivedDate\":\"2022-07-14\",\"pageNumber\":0}'}",createHttpHeaders());
		Member member = new Member();
		PcpMember pcpMember = new PcpMember();
		pcpMember.setContracts(List.of(getMemberContract()));
		member.setPcpMembers(pcpMember);
		ResponseEntity<Member> response  = new ResponseEntity(member, HttpStatus.OK);

		doReturn(response).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.POST), Mockito.<HttpEntity>eq(entity), Mockito.<Class<Member>>any());

		
		List<ContractEntity> contractEntity = tibcoClient.fetchContracts(param);

		Assertions.assertNotNull(contractEntity);
	
	}
	
	@Test
	public void testFetchContractWhenResponseIsNull() {
		ReflectionTestUtils.setField(tibcoClient, "tibcoPcpMemberUrl", "http://test.com", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthUser", "testUser", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthPassword", "testPass", String.class);
		ReflectionTestUtils.setField(tibcoClient, "tibcoQueryStr", "{'memberProviderAssignment':'{\"states\":[${state}],\"numberOfDays\":${numofdays},\"receivedDate\":\"${receiveddate}\",\"pageNumber\":${pagenum}}'}", String.class);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.parse("07-14-2022",df).toString());
		param.put("pagenum", "0");
		HttpEntity<?> entity = new HttpEntity<>("{'memberProviderAssignment':'{\"states\":[AL],\"numberOfDays\":5,\"receivedDate\":\"2022-07-14\",\"pageNumber\":0}'}",createHttpHeaders());
		Member member = new Member();
		PcpMember pcpMember = new PcpMember();
		pcpMember.setContracts(List.of(getMemberContract()));
		member.setPcpMembers(pcpMember);
		ResponseEntity<Member> response  = new ResponseEntity(member, HttpStatus.OK);

		doReturn(response).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.POST), Mockito.<HttpEntity>eq(entity), Mockito.<Class<Member>>any());

		
		List<ContractEntity> contractEntity = tibcoClient.fetchContracts(param);

		Assertions.assertNotNull(contractEntity);
	
	}
	
	@Test
	public void testFetchContractWhenResponseThrowExcepton() {
		ReflectionTestUtils.setField(tibcoClient, "tibcoPcpMemberUrl", "http://test.com", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthUser", "testUser", String.class);
		ReflectionTestUtils.setField(tibcoClient, "basicAuthPassword", "testPass", String.class);
		ReflectionTestUtils.setField(tibcoClient, "tibcoQueryStr", "{'memberProviderAssignment':'{\"states\":[${state}],\"numberOfDays\":${numofdays},\"receivedDate\":\"${receiveddate}\",\"pageNumber\":${pagenum}}'}", String.class);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		Map<String, String> param = new HashMap<>();
		param.put("state", "CA");
		param.put("numofdays", "5");
		param.put("receiveddate", LocalDate.parse("07-14-2022",df).toString());
		param.put("pagenum", "0");
		HttpEntity<?> entity = new HttpEntity<>("{'memberProviderAssignment':'{\"states\":[CA],\"numberOfDays\":5,\"receivedDate\":\"2022-07-14\",\"pageNumber\":0}'}",createHttpHeaders());
		Member member = new Member();
		PcpMember pcpMember = new PcpMember();
		pcpMember.setContracts(List.of(getMemberContract()));
		member.setPcpMembers(pcpMember);
		ResponseEntity<Member> response  = new ResponseEntity(member, HttpStatus.OK);

		doThrow(InternalServerError.class).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.POST), Mockito.<HttpEntity>eq(entity), Mockito.<Class<Member>>any());
		List<ContractEntity> contractEntity = tibcoClient.fetchContracts(param);
		Mockito.verify(restTemplate, atLeastOnce()).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.POST), Mockito.<HttpEntity>eq(entity), Mockito.<Class<Member>>any());

	
	}
	
	@Test
	public void testRecover() {
		tibcoClient.recover(new RuntimeException(), "{'memberProviderAssignment':'{\"states\":[AL],\"numberOfDays\":5,\"receivedDate\":\"2022-07-14\",\"pageNumber\":0}'}");
	
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
	
	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("testUser", "testPass");
		return headers;
	}

}
