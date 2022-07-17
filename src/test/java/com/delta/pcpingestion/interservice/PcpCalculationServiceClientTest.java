package com.delta.pcpingestion.interservice;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import java.util.List;

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
import com.delta.pcpingestion.enums.PCPMemberIngestionErrors;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.deltadental.platform.common.exception.ServiceException;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Pcp Calculation Service Client Test")
public class PcpCalculationServiceClientTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private PcpCalculationServiceClient pcpCalculationServiceClient;

	@Test
	public void testPubllish() throws Exception {
		ReflectionTestUtils.setField(pcpCalculationServiceClient, "pcpCalculationServiceEndpoint", "http://test.com");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MemberContractClaimRequest> list = List.of();
		HttpEntity<?> entity = new HttpEntity<>(list, headers);
		ResponseEntity<Boolean> response = new ResponseEntity(Boolean.TRUE, HttpStatus.OK);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString("http://test.com/members-contracts-and-claims");
		String uriBuilder = builder.build().encode().toUriString();

		ReflectionTestUtils.setField(pcpCalculationServiceClient, "pcpCalculationServiceEndpoint", "https://test.com");

		doReturn(response).when(restTemplate).exchange(ArgumentMatchers.any(), Mockito.<HttpMethod>eq(HttpMethod.POST),
				Mockito.<HttpEntity>eq(entity), Mockito.<Class<Boolean>>any());

		pcpCalculationServiceClient.publish(list);
		Mockito.verify(restTemplate, times(2)).exchange(ArgumentMatchers.any(), Mockito.<HttpMethod>eq(HttpMethod.POST),
				Mockito.<HttpEntity>eq(entity), Mockito.<Class<Boolean>>any());
	}

	@Test
	public void testPubllishWhenException() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MemberContractClaimRequest> list = List.of();
		HttpEntity<?> entity = new HttpEntity<>(list, headers);
		ResponseEntity<Boolean> response = new ResponseEntity(Boolean.TRUE, HttpStatus.OK);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString("http://test.com/members-contracts-and-claims");
		String uriBuilder = builder.build().encode().toUriString();

		ReflectionTestUtils.setField(pcpCalculationServiceClient, "pcpCalculationServiceEndpoint", "https://test.com");

		pcpCalculationServiceClient.recover(new RuntimeException(), List.of(new ContractEntity()));
		pcpCalculationServiceClient.recoverPublishAssignMemberPCP(new RuntimeException(),
				new MemberContractClaimRequest());
		pcpCalculationServiceClient.recoverPublishAssignMembersPCP(new RuntimeException(),
				List.of(new MemberContractClaimRequest()));

		doThrow(InternalServerError.class).when(restTemplate).exchange(ArgumentMatchers.any(),
				Mockito.<HttpMethod>eq(HttpMethod.POST), Mockito.<HttpEntity>eq(entity), Mockito.<Class<Boolean>>any());

		try {
			pcpCalculationServiceClient.publish(list);
		} catch (ServiceException exception) {
			Assertions.assertEquals(exception.getErrorCode().toString(),
					PCPMemberIngestionErrors.PCP_SERVICE_ERROR.name());
		}
	}

}
