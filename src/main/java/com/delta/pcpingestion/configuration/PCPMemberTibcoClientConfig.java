package com.delta.pcpingestion.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
//
//@Component
//@Slf4j
public class PCPMemberTibcoClientConfig {
//  @Value("${pcp.ingestion.service.basicAuthUser}")
//  String tibcoServiceUserId;
//
//  @Value("${pcp.ingestion.service.basicAuthPassword}")
//  String tibcoServicePassword;
//
//  @Value("${tibco.connection.timeout:60000}")
//  Integer connectTimeOut;
//
//  @Value("${tibco.read.timeout:60000}")
//  Integer readTimeOut;
//
//  @Value("${service.cert.file}")
//  String serviceCertFile;
//
//  @Value("${service.cert.password}")
//  String serviceCertPassword;
//
//  @Autowired RestTemplateBuilder builder;
//
//  @Primary
//  @Bean
//  public RestTemplate tibcoRestTemplate()
//      throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
//          KeyManagementException {
//    return new RestTemplate(
//        httpComponentsClientHttpRequestFactory(serviceCertFile, serviceCertPassword));
//  }
//
//  private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(
//      String certFilePath, String restServiceCertPwd)
//      throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
//          KeyManagementException {
//    CredentialsProvider provider = new BasicCredentialsProvider();
//    UsernamePasswordCredentials credentials =
//        new UsernamePasswordCredentials(tibcoServiceUserId, tibcoServicePassword);
//    log.info("Tibco user {}", tibcoServiceUserId);
//    provider.setCredentials(AuthScope.ANY, credentials);
//
//    CloseableHttpClient httpClient =
//        HttpClients.custom()
//            .setSSLSocketFactory(sslsf(certFilePath, restServiceCertPwd))
//            .setMaxConnTotal(1000)
//            .setMaxConnPerRoute(100)
//            .setConnectionTimeToLive(connectTimeOut, TimeUnit.MILLISECONDS)
//            .setDefaultCredentialsProvider(provider)
//            .build();
//    return new HttpComponentsClientHttpRequestFactory(httpClient);
//  }
//
//  private SSLConnectionSocketFactory sslsf(String certFilePath, String restServiceCertPwd)
//      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
//          KeyManagementException {
//    SSLContext sslcontext;
//    KeyStore trustStore;
//    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//    try (InputStream certFileStream = new FileInputStream(certFilePath)) {
//      trustStore.load(certFileStream, restServiceCertPwd.toCharArray());
//      sslcontext =
//          SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
//    }
//    return new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
//  }
}
