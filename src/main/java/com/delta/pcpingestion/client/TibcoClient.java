package com.delta.pcpingestion.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.dto.Member;
import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.mapper.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TibcoClient {

	@Value("${pcp.ingestion.service.tibcoPcpMemberUrl}")
	private String tibcoPcpMemberUrl;

	@Value("${pcp.ingestion.service.basicAuthUser}")
	private String basicAuthUser;

	@Value("${pcp.ingestion.service.basicAuthPassword}")
	private String basicAuthPassword;
	
	
	private String tibcoQueryStr = "{'pcpMembersRequest':'{\"states\":[${state}],\"numofdays\":${numofdays},\"receiveddate\":\"${receiveddate}\",\"pagenum\":${pagenum}}'}";

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Mapper mapper;

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${pcp.tibco.service.retry.maxattempts:3}")
	public List<ContractEntity> fetchContracts(Map<String, String> params) {
		
		String tibcoRequest = StrSubstitutor.replace(tibcoQueryStr, params);
		log.info("Member call for tibco {}", tibcoRequest);
		List<ContractEntity> contracts = List.of();
		ResponseEntity<Member> response = callTibco(tibcoRequest);
		
		if (null != response && null != response.getBody() && null != response.getBody().getPcpMembers()) {
			contracts = mapper.map(response.getBody().getPcpMembers().getContracts());
		}
		return contracts;
	}
	private ResponseEntity<Member> callTibco(String tibcoQueryStr) {
		log.info("Tibco Client Call to get records......");
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tibcoPcpMemberUrl);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(basicAuthUser, basicAuthPassword);
		ResponseEntity<Member> response = null;
		try {
			response = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,
					new HttpEntity<>(tibcoQueryStr, headers), Member.class);
		} catch (RestClientException e) {
			throw new RuntimeException("Exception in Rest client {}", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Exception in URI Syntax Exception {}", e);
		}
		return response;
	}

	@Recover
	public ResponseEntity<Member> recover(RuntimeException t, String tibcoQueryStr) {
		log.info("Alert - Tibco Service is not responding......");
		return null;
	}
}
