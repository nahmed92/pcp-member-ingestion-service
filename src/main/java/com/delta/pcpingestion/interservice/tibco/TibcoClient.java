package com.delta.pcpingestion.interservice.tibco;

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

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.tibco.dto.Member;
import com.delta.pcpingestion.mapper.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TibcoClient {

	@Value("${pcp.ingestion.service.tibco.url}")
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
		log.info("START TibcoClient.fetchContracts()");
		String tibcoRequest = StrSubstitutor.replace(tibcoQueryStr, params);
		List<ContractEntity> contracts = List.of();
		ResponseEntity<Member> response = callTibco(tibcoRequest);
		
		if (null != response && null != response.getBody() && null != response.getBody().getPcpMembers()) {
			contracts = mapper.map(response.getBody().getPcpMembers().getContracts());
		}
		log.info("END TibcoClient.fetchContracts()");
		return contracts;
	}
	
	private ResponseEntity<Member> callTibco(String tibcoQueryStr) {
		log.info("START TibcoClient.callTibco()");
		
		String uriBuilder = UriComponentsBuilder.fromUriString(tibcoPcpMemberUrl).build().encode().toUriString();
		ResponseEntity<Member> response = null;
		try {
			log.info("Calling tibco request {}",tibcoQueryStr);
			response = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,
					new HttpEntity<>(tibcoQueryStr, createHttpHeaders()), Member.class);
			log.info("Got tibco response {}",response);
			
		} catch (RestClientException e) {
			throw new RuntimeException("Exception in Rest client {}", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Exception in URI Syntax Exception {}", e);
		}
		log.info("END TibcoClient.callTibco()");
		return response;
	}
	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(basicAuthUser, basicAuthPassword);
		return headers;
	}

	@Recover
	public ResponseEntity<Member> recover(RuntimeException t, String tibcoQueryStr) {
		log.info("Alert - Tibco Service is not responding......");
		return null;
	}
}
