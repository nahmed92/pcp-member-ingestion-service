package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PCPConfigServiceClient;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

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
		executor = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount);
	}

	@MethodExecutionTime
	public void ingest() {
		log.info("START PCPIngestionService.ingest()");

		ingestFromTibco();

		log.info("END PCPIngestionService.ingest()");
	}

	public void ingestFromTibco() {
		log.info("START PCPIngestionService.ingestFromTibco()");
		String lookbackDays = configClient.providerLookBackDays();
		LocalDate cutOffDate = LocalDate.now().minusDays(Integer.parseInt(lookbackDays));
		for (State state : State.values()) {
			executor.submit(() -> {
				ingestFromTibco(state, cutOffDate, numOfDays);

			});
		}
		log.info("END PCPIngestionService.ingestFromTibco()");
	}

	private void ingestFromTibco(State state, LocalDate cutOffDate, Integer numOfDays2) {
		contractIngester.ingestByState(state, cutOffDate, numOfDays);

	}

}
