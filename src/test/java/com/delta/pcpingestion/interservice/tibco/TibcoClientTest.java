package com.delta.pcpingestion.interservice.tibco;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.tibco.dto.Member;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class TibcoClientTest {
	
	@Mock
	private RestTemplate restTemplate;
	
	@Test
	public void Given_UserIsAuthenticated_When_ValidRequestToGetHolidayData_Then_HolidayDataWillReturn() {
		
//		ContractEntity entity = ContractEntity.builder() //
//				.claimIds("1234").contractId("3456").id(UUID.randomUUID().toString()).build();
//
//		HttpHeaders resHeaders = new HttpHeaders();
//		resHeaders.set("Accept", "application/json");
//		ResponseEntity<ContractEntity> res = new ResponseEntity<ContractEntity>(Lists.lis(entity), resHeaders,
//				HttpStatus.OK);
//		when(restTemplate.exchange(Mockito.any(), Mockito.<HttpMethod>eq(HttpMethod.GET),
//				Mockito.<HttpEntity<Member>>any(), Mockito.<Class<Member>>any())).thenReturn(res);
//		assertNotNull(contentServiceClient.getHolidays(Boolean.TRUE));
//
//		assertNotNull(res.getBody().getContents());
//		assertNotNull(res.getBody().getContentType());
//		assertNotNull(res.getBody().getTenantName());
//		assertNotNull(res.getBody().getApplicationName());
//		assertNotNull(res.getBody().getContents().get(0).getCalendarYear());
	}

}
