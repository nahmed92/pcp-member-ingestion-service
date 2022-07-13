package com.delta.pcpingestion.repo;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;

//@SpringBootTest
public class IngestionControllerRepositoryTest {

	@Autowired
	IngestionControllerRepository repo;
	
	@BeforeEach
	public void init() {
		IngestionControllerEntity entity = IngestionControllerEntity.builder().id("123")
				.runId("123")
				.serviceInstanceId("localhost")
				.status(ControlStatus.CREATED)
				.states("CA,NY")
				.build();
		
		repo.save(entity);
	}
	
	//@Test
	public void testFindAll() {
		List<IngestionControllerEntity> entities =  repo.findAll();
		
		assertNotNull(entities);
		
		
		
		
		
		
	}
}
