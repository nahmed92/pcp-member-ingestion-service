package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PCPConfigServiceClient;
import com.delta.pcpingestion.repo.IngestionControllerRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Component
public class IngestionService {

	@Autowired
	private PCPConfigServiceClient configClient;

	@Autowired
	private ContractIngester contractIngester;
	
	@Autowired
	private IngestionControllerRepository repo;


	@Value("${pcp.ingestion.process.workers.count:4}")
	private Integer pcpIngestionProcessWorkersCount;

	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;
	
	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private ExecutorService executor;

	@PostConstruct
	public void init() {
		 
		ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("Ingestion-tp-%d").build();
		executor = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount, tf);
		
	}

	@MethodExecutionTime
	@Async
	public void ingest() {
		log.info("START PCPIngestionService.ingest()");

		//ingestFromTibco();
		ingestFromController();
		
		log.info("END PCPIngestionService.ingest()");
	}

	@Transactional
	private void ingestFromController() {
		log.info("START IngestionService.ingestFromController()");
		
		//FIXME: loop while u get null results
		//FIXME: submit to executor
		boolean recordPresent = false;
		do {
			Optional<IngestionControllerEntity> entityOptional = repo.readCreated();
			
			if(entityOptional.isPresent()) {
				recordPresent=true;
				IngestionControllerEntity entity=entityOptional.get();
				executor.submit(() -> {
					ingest(entity);	
				});
				
			}else {
				recordPresent=false;
			}
				
		}while(recordPresent);
		
		
		log.info("END IngestionService.ingestFromController()");
		
	}

	private void ingest(IngestionControllerEntity entity) {
		entity.setStatus(ControlStatus.IN_PROGRESS);
		entity.setServiceInstanceId(serviceInstanceId);
		repo.save(entity);
		
		contractIngester.ingest(entity);
		entity.setStatus(ControlStatus.COMPLETED);
		repo.save(entity);
	}

}
