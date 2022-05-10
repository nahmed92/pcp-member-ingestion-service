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

import com.delta.pcpingestion.entity.ContractEntity;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PcpCalculationServiceClient {

	@Value("${pcp.calculation.service.endpoint}")
	private String pcpCalculationServiceEndpoint;

	@Autowired(required = true)
	private RestTemplate restTemplate;

	public ValidateProviderResponse validateProvider() {
		log.info("Call PCP Calculation Service Validate Provider");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(pcpCalculationServiceEndpoint + "/validate-provider");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<ValidateProviderResponse> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST, new HttpEntity<>(headers),
					ValidateProviderResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		return responseEntity.getBody();

	}

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.calculation.service.retry.maxattempts:3}")
	public void publishContractToPcpCalcuationService(List<ContractEntity> contracts) throws RuntimeException {
		log.info("Call PCP Calculation Service Client.....");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(pcpCalculationServiceEndpoint + "/process-pcp-member-contract");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST, new HttpEntity<>(contracts, headers),
					new ParameterizedTypeReference<List<ContractEntity>>() {
					});
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}

	}

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.calculation.service.retry.maxattempts:3}")
	public ResponseEntity<ValidateProviderResponse> publishAssignMemberPCP(ValidateProviderRequest validateAssignMember)
			throws RuntimeException {
		log.info("Call PCP Calculation Service Client.....");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(pcpCalculationServiceEndpoint + "/member-contract-claim");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<ValidateProviderResponse> response = null;
		try {
			response = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,
					new HttpEntity<>(validateAssignMember, headers), ValidateProviderResponse.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		return response;
	}

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.calculation.service.retry.maxattempts:3}")
	public ResponseEntity<MessageResponse> publishAssignMembersPCP(List<ValidateProviderRequest> validateAssignMembers)
			throws RuntimeException {
		log.info("START PcpCalculationServiceClient.publishAssignMembersPCP()");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(pcpCalculationServiceEndpoint + "/members-contracts-and-claims");
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<MessageResponse> response = null;
		try {
			response = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,
					new HttpEntity<>(validateAssignMembers, headers), MessageResponse.class);

		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to publish ", e);
			throw new RuntimeException("Rest Client Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		} catch (Exception e) {
			log.error("Unable to publish ", e);
			throw new RuntimeException("URI Syntax Exception [" + e.getCause() + "] and Messagge [" + e.getMessage());
		}
		log.info("END PcpCalculationServiceClient.publishAssignMembersPCP()");
		return response;
	}

	@Recover
	public void recover(RuntimeException t, List<ContractEntity> contracts) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
	}

	@Recover
	public ResponseEntity<ValidateProviderResponse> recoverPublishAssignMemberPCP(RuntimeException t,
			ValidateProviderRequest validateProviderRequest) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
		return null;
	}

	@Recover
	public ResponseEntity<MessageResponse> recoverPublishAssignMembersPCP(RuntimeException t,
			List<ValidateProviderRequest> validateAssignMembers) {
		log.info("Alert - PCP-Calculation-Service is not responding......");
		return null;
	}
}
