package com.delta.pcpingestion.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ComponentScan(basePackages = { "com.delta.pcpingestion.*" })
public class ApplicationConfig {

	@Value("${ingestion.workers.count:2}")
	private Integer ingestionWorkersCount;

	@Value("${publisher.workers.count:2}")
	private Integer publisherWorkerCount;

	@Bean("publisherExecutor")
	public ExecutorService initPublisherService() {
		ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("publish-tp-%d").build();
		return Executors.newFixedThreadPool(publisherWorkerCount, tf);
	}

	@Bean("ingestionExecutor")
	public ExecutorService createIngestionExecutor() {
		ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("ingest-tp-%d").build();
		return Executors.newFixedThreadPool(ingestionWorkersCount, tf);
	}

	@Bean
	public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
//		setMessageConverter(restTemplate);
		return restTemplate;
	}
}
