package com.delta.pcpingestion.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.interservice.PcpCalculationServiceClient;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.delta.pcpingestion.mapper.Mapper;
import com.delta.pcpingestion.repo.ContractDAO;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Testing Publish Contract Service Impl")
@Configuration
@Slf4j
public class PublisherServiceTest {
	
	@Mock
	private ContractDAO repository;
	
	@Mock
	private PcpCalculationServiceClient pcpCalculationClient;
	
	@Mock
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	@Mock
	private Mapper mapper;
	
	
	@InjectMocks
	private PublisherService publishContract;
	
	@Test
	public void testPublish() throws Exception {
		ReflectionTestUtils.setField(publishContract, "numOfDays", 5);
		ContractEntity entity = ContractEntity.builder() //
				.claimIds("1234").contractId("3456").id(UUID.randomUUID().toString()).build();
		doReturn(Lists.list(entity)).when(repository).findByPublishStatusAndStateCode(PublishStatus.STAGED.name(), "AL");
		publishContract.publish();	
		Mockito.verify(repository, times(1)).findByPublishStatusAndStateCode(PublishStatus.STAGED.name(), "AL");
	}
	
	//@Test
	public void testPublishMethod() throws Exception {
		ContractEntity entity = ContractEntity.builder() //
				.claimIds("1234").contractId("3456").id(UUID.randomUUID().toString()).build();
	ReflectionTestUtils.setField(publishContract, "mapper", mapper, Mapper.class);
	MemberContractClaimRequest request = new MemberContractClaimRequest();
	request.setClaimId("12343");
	
	PublisherService publishService = new PublisherService();
	Mapper mapper = new Mapper();
	Mapper mapperMock = Mockito.spy(mapper);
	Mockito.doReturn(List.of(request)).when(mapperMock).mapRequest(entity);
	Class[] param = new Class[1];
	param[0] = ContractEntity.class;
	Method methodCall = publishService.getClass().getDeclaredMethod("publish", param);
	methodCall.setAccessible(true);
	Object[] argument = new Object[1];
	argument[0] = entity;
	methodCall.invoke(publishService, argument);
	}
}
