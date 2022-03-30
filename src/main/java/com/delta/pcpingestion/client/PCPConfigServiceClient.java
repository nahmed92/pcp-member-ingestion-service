package com.delta.pcpingestion.client;

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


import lombok.extern.slf4j.Slf4j;

@Service("pcpConfigService")
@Slf4j
public class PCPConfigServiceClient {

	@Value("${pcp.config.service.endpoint}")
	private String pcpConfigServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public String providerLookaheadDays() {
		log.info("START PCPConfigService.providerLookaheadDays");
		String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.PROVIDER_LOOKAHEAD_DAYS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.providerLookaheadDays");
		return responseEntity.getBody();
	}
	
	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.config.service.retry.maxattempts:3}")
	public String providerLookBackDays() {
		log.info("START PCPConfigService.providerLookaheadDays");
		String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.CLAIM_LOOKBACK_DAYS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.providerLookaheadDays");
		return responseEntity.getBody();
	}
	
	public String explanationCode() {
		log.info("START PCPConfigService.explanationcode");
		final String explanationCode = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.EXPLANATION_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(explanationCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.explanationcode");
		return responseEntity.getBody();
	}
	
	public String procedureCode() {
		log.info("START PCPConfigService.explanationcode");
		final String procedureCode = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.PROCEDURE_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(procedureCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.explanationcode");
		return responseEntity.getBody();
	}
	
	public String claimStatus() {
		log.info("START PCPConfigService.claimStatus");
		final String claimStatusUrl = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.CLAIM_STATUS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(claimStatusUrl);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPConfigService.claimStatus");
		return responseEntity.getBody();
	}
	
	
	private static void setMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));  
		messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
//	
//	public PCPValidateResponse claimStatus(PcpAssignmentRequest pcpAssignmentRequest) {
//		log.info("START PCPSearchService.validateProvider");
//		String providerValidateEndPoint = pcpSearchServiceEndpoint.concat(PCPSearchServiceConstants.PROVIDER_VALIDATION);
//		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerValidateEndPoint);
//		String uriBuilder = builder.build().encode().toUriString();
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		ResponseEntity<PCPValidateResponse> responseEntity = null;
//		try {
//			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,  new HttpEntity<>(pcpAssignmentRequest, headers), PCPValidateResponse.class);
//		} catch (RestClientException e) {
//			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
//		} catch (URISyntaxException e) {
//			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
//		}
//		if(responseEntity.getStatusCode() == HttpStatus.OK) {
//			return responseEntity.getBody();
//		} 
//		log.info("END PCPSearchService.validateProvider");
//		return null;
//	}
//	
//	
//	public PCPValidateResponse explanationCode(PcpAssignmentRequest pcpAssignmentRequest) {
//		log.info("START PCPSearchService.validateProvider");
//		String providerValidateEndPoint = pcpSearchServiceEndpoint.concat(PCPSearchServiceConstants.PROVIDER_VALIDATION);
//		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerValidateEndPoint);
//		String uriBuilder = builder.build().encode().toUriString();
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		ResponseEntity<PCPValidateResponse> responseEntity = null;
//		try {
//			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,  new HttpEntity<>(pcpAssignmentRequest, headers), PCPValidateResponse.class);
//		} catch (RestClientException e) {
//			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
//		} catch (URISyntaxException e) {
//			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
//		}
//		if(responseEntity.getStatusCode() == HttpStatus.OK) {
//			return responseEntity.getBody();
//		} 
//		log.info("END PCPSearchService.validateProvider");
//		return null;
//	}
	
	@Recover
	public String recoverProviderLookBackDays(RuntimeException t) {
		log.info("Alert - PCP-Config-Service is not responding......");
		return null;
	}

}