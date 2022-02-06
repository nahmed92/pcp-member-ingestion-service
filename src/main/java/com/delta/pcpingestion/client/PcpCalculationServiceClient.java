package com.delta.pcpingestion.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.entity.PCPMemberContractEntity;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PcpCalculationServiceClient {
	
	@Value("${pcp.calculation.service.endpoint}")
	private String pcpCalculationServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public ValidateProviderResponse validateProvider() {
		log.info("Call PCP Calculation Service Validate Provider");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(pcpCalculationServiceEndpoint+"/validate-provider");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<ValidateProviderResponse> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST, 
					new HttpEntity<>(headers), ValidateProviderResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		}
			return responseEntity.getBody();
	
	}
	
	@Retryable(value = RuntimeException.class ,  maxAttemptsExpression = "${pcp.calculation.service.retry.maxattempts:3}")
	public void publishContractToPcpCalcuationService(List<PCPMemberContractEntity> contracts) throws RuntimeException{
		log.info("Call PCP Calculation Service Client.....");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(pcpCalculationServiceEndpoint+"/process-pcp-member-contract");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST, 
					new HttpEntity<>(contracts,headers),
					 new ParameterizedTypeReference<List<PCPMemberContractEntity>>() {});
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception ["+e.getCause()+"] and Messagge ["+e.getMessage());
		}

	}
	
    @Recover
    public void recover(RuntimeException t,List<PCPMemberContractEntity> contracts){
        log.info("Alert - PCP-Calculation-Service is not responding......");
    }
}
