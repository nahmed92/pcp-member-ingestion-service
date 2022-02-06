package com.delta.pcpingestion.client;

import java.net.URI;
import java.net.URISyntaxException;

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
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Retryable(value = RuntimeException.class ,  maxAttemptsExpression = "${pcp.tibco.service.retry.maxattempts:3}")
	public ResponseEntity<Member> fetchPcpmemberFromTibco(String tibcoQueryStr) {
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
    public ResponseEntity<Member> recover(RuntimeException t, String tibcoQueryStr){
        log.info("Alert - Tibco Service is not responding......");
        return null;
    }
}
