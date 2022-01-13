package com.delta.pcpingestion.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.delta.pcpingestion.dto.Member;

@Component
public class TibcoClient {

	@Value("${pcp.ingestion.service.tibcoPcpMemberUrl}")
	private String tibcoPcpMemberUrl;
	
	@Value("${pcp.ingestion.service.basicAuthUser}")
	private String basicAuthUser;
	
	@Value("${pcp.ingestion.service.basicAuthPassword}")
	private String basicAuthPassword;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	public ResponseEntity<Member> fetchPcpmemberFromTibco(String tibcoQueryStr) {
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
}
