package com.delta.pcpingestion.interservice.tibco;

import java.net.URI;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.interservice.tibco.dto.Member;
import com.delta.pcpingestion.mapper.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TibcoClient {

	@Value("${tibco.client.url}")
	private String tibcoPcpMemberUrl;

	@Value("${tibco.client.user}")
	private String basicAuthUser;

	@Value("${tibco.client.password}")
	private String basicAuthPassword;

	private String tibcoQueryStr = "{'memberProviderAssignment':'{\"states\":[${state}],\"numberOfDays\":${numofdays},\"receivedDate\":\"${receiveddate}\",\"pageNumber\":${pagenum}}'}";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Mapper mapper;

	@Retryable(value = RuntimeException.class, maxAttemptsExpression = "${tibco.call.retry.max.attempts:3}")
	public List<ContractEntity> fetchContracts(Map<String, String> params) {
		log.info("START TibcoClient.fetchContracts()");
		String tibcoRequest = StrSubstitutor.replace(tibcoQueryStr, params);
		List<ContractEntity> contracts = List.of();
		ResponseEntity<Member> response = callTibco(tibcoRequest);

		if (response != null && response.getBody() != null) {
			Member member = response.getBody();
			if(member != null && member.getPcpMembers() != null) {
			contracts = mapper.map(member.getPcpMembers().getContracts());
			}
		}
		log.info("END TibcoClient.fetchContracts()");
		return contracts;
	}

	private ResponseEntity<Member> callTibco(String tibcoQueryStr) {
		log.info("START TibcoClient.callTibco()");

		String uriBuilder = UriComponentsBuilder.fromUriString(tibcoPcpMemberUrl).build().encode().toUriString();
		ResponseEntity<Member> response = null;
		try {
			log.info("Calling tibco request {}", tibcoQueryStr);
			response = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,
					new HttpEntity<>(tibcoQueryStr, createHttpHeaders()), Member.class);
			log.info("Got tibco response {}", response);

		} catch (Exception e) {
			log.error("Unable get data from TIBCO ", e);
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
