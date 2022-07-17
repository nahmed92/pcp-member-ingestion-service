package com.delta.pcpingestion.interservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.enums.PCPMemberIngestionErrors;

import lombok.extern.slf4j.Slf4j;

@Service("pcpConfigService")
@Slf4j
public class PCPConfigServiceClient {

	@Value("${pcp.config.service.endpoint}")
	private String pcpConfigServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.config.service.retry.max.attempts:3}")
	public String claimLookBackDays() {
		log.info("START PCPConfigService.claimLookBackDays");
		String claimLookBackDaysEndPoint = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.CLAIM_LOOKBACK_DAYS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(claimLookBackDaysEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		 } catch (RestClientException | URISyntaxException e) {
	            log.error("Error calling Config service request {}", e);
	            throw PCPMemberIngestionErrors.PCP_SERVICE_ERROR.createException();
	    }
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.claimLookBackDays");
		return responseEntity.getBody();
	}
	
	private static void setMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();        
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));  
		messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
	
	@Recover
	public String recoverProviderLookBackDays(RuntimeException t) {
		log.info("Alert - PCP-Config-Service is not responding......");
		return null;
	}

}
