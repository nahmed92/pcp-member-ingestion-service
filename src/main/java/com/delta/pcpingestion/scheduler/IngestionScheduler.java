package com.delta.pcpingestion.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.service.IngestionService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IngestionScheduler {

	@Autowired
	private IngestionService ingestionService;
	
	//@Scheduled(cron = "0 0 * * * *")
	//@Scheduled(cron = "${scheduling.job.ingestion.cron}")
	@Scheduled(initialDelayString = "${scheduling.job.ingestion.delay}", fixedDelayString = "${scheduling.job.ingestion.delay}")
	@MethodExecutionTime
	public void ingest() {
		log.info("START IngestionScheduler.ingest()");
	
		ingestionService.ingest();
		
		log.info("END IngestionScheduler.ingest()");
	}
}
