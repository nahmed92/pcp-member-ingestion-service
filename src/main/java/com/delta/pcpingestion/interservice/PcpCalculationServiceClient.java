package com.delta.pcpingestion.interservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PcpCalculationServiceClient {

	@Value("${pcp.calculation.service.endpoint}")
	private String pcpCalculationServiceEndpoint;

	@Autowired(required = true)
	private RestTemplate restTemplate;
 

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.calculation.service.retry.max.attempts:3}")
	public void publish(List<MemberContractClaimRequest> request)
			throws RuntimeException {
		log.info("START PcpCalculationServiceClient.publish()");
		
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(pcpCalculationServiceEndpoint + "/members-contracts-and-claims");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			
			log.info("Calling calculation service, Request {}",request);
			
			ResponseEntity<Boolean> response  = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,new HttpEntity<>(request, headers), Boolean.class);
			
			log.info("Got Response from calculation service, response code {}",response.getStatusCode());

		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to publish ", e);
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (Exception e) {
			log.error("Unable to publish ", e);
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		log.info("END PcpCalculationServiceClient.publish()");
	}

	@Recover
	public void recover(RuntimeException t, List<ContractEntity> contracts) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
	}

	@Recover
	public ResponseEntity<MemberContractClaimRequest> recoverPublishAssignMemberPCP(RuntimeException t,
			MemberContractClaimRequest validateProviderRequest) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
		return null;
	}

	@Recover
	public ResponseEntity<Boolean> recoverPublishAssignMembersPCP(RuntimeException t,
			List<MemberContractClaimRequest> validateAssignMembers) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
		return null;
	}
}
