package com.delta.pcpingestion.interservice;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

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

import com.delta.pcpingestion.enums.PCPMemberIngestionErrors;
import com.deltadental.platform.common.exception.ServiceException;

import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("WhenPCP Config Service Client Test")
public class PCPConfigServiceClientTest {


	@Mock
	private RestTemplate restTemplate;	
	
	@InjectMocks
	private PCPConfigServiceClient pcpConfigServiceClient;
	
	
	@Test
	public void testClaimLookBackDays() throws Exception{
		ReflectionTestUtils.setField(pcpConfigServiceClient, "pcpConfigServiceEndpoint", "http://test.com");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response  = new ResponseEntity("99", HttpStatus.OK);

		doReturn(response).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.GET), Mockito.<HttpEntity>eq(entity) , Mockito.<Class<String>>any());
		
		pcpConfigServiceClient.claimLookBackDays();
		Mockito.verify(restTemplate, atLeastOnce()).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.GET), Mockito.<HttpEntity>eq(entity) , Mockito.<Class<String>>any());
	}
	
	@Test
	public void testClaimLookBackDaysStatusNotOk() throws Exception{
		ReflectionTestUtils.setField(pcpConfigServiceClient, "pcpConfigServiceEndpoint", "http://test.com");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response  = new ResponseEntity("99", HttpStatus.BAD_REQUEST);
		
		pcpConfigServiceClient.recoverProviderLookBackDays(new RuntimeException());

		doReturn(response).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.GET), Mockito.<HttpEntity>eq(entity) , Mockito.<Class<String>>any());
		
		pcpConfigServiceClient.claimLookBackDays();
		Mockito.verify(restTemplate, atLeastOnce()).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.GET), Mockito.<HttpEntity>eq(entity) , Mockito.<Class<String>>any());
	}
	
	@Test
	public void testClaimLookBackDaysThrowException() throws Exception{
		ReflectionTestUtils.setField(pcpConfigServiceClient, "pcpConfigServiceEndpoint", "http://test.com");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response  = new ResponseEntity("99", HttpStatus.OK);

		doThrow(InternalServerError.class).when(restTemplate).exchange(ArgumentMatchers.any(), 
				Mockito.<HttpMethod>eq(HttpMethod.GET), Mockito.<HttpEntity>eq(entity) , Mockito.<Class<String>>any());
		try {
		pcpConfigServiceClient.claimLookBackDays();
		} catch(ServiceException exception) {
    		Assertions.assertEquals(exception.getErrorCode().toString() ,
    				PCPMemberIngestionErrors.PCP_SERVICE_ERROR.name());
		}
	}
	
}
