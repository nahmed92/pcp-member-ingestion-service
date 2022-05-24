package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PCPConfigServiceClient;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
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

	@Value("${pcp.ingestion.process.workers.count:8}")
	private Integer pcpIngestionProcessWorkersCount;

	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;

	private ExecutorService executor;

	@PostConstruct
	public void init() {
		 
		ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("Ingestion-tp-%d").build();
		executor = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount, tf);
		
	}

	@MethodExecutionTime
	@Synchronized
	public void ingest() {
		log.info("START PCPIngestionService.ingest()");

		ingestFromTibco();
		
		log.info("END PCPIngestionService.ingest()");
	}

	public void ingestFromTibco() {
		log.info("START PCPIngestionService.ingestFromTibco()");
		String lookbackDays = configClient.claimLookBackDays();
		LocalDate cutOffDate = LocalDate.now().minusDays(Integer.parseInt(lookbackDays));
		
		log.info("lookbackDays {}, cutOffDate {}",lookbackDays,cutOffDate);
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		CountDownLatch latch = new CountDownLatch(State.values().length);
		
		for (State state : State.values()) {
			 executor.submit(() -> {
				 try {
					 contractIngester.ingestByState(state, cutOffDate, numOfDays);
				 }finally {
					latch.countDown();
				}
			});
		}
		try {
			latch.await(); // wait for all tasks to complete
		} catch (Exception e) {
			//Do nothing
		}
		stopwatch.stop();
		log.info("Completed ingestion in {}  seconds ",stopwatch.elapsed(TimeUnit.SECONDS));
		
		log.info("END PCPIngestionService.ingestFromTibco()");
	}

}
