package com.delta.pcpingestion.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PcpCalculationServiceClient {
	
	@Value("${pcp.calculation.service.endpoint}")
	private String pcpCalculationServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public ValidateProviderResponse validateProvider() {
		log.info("START PCPSearchService.validateProvider");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(pcpCalculationServiceEndpoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<ValidateProviderResponse> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,  new HttpEntity<>(headers), ValidateProviderResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		}
			return responseEntity.getBody();
	
	}

}
