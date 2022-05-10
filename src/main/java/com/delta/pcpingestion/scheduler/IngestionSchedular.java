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
public class IngestionSchedular {

	@Autowired
	private IngestionService ingestionService;

	@Scheduled(initialDelayString = "${job.pcp.contract.initial.delay}", fixedRateString = "${job.pcp.contract.fixed.delay}")
	@MethodExecutionTime
	public void scheduleIngest() {
		log.info("START IngestionSchedular.scheduleIngest()");
	
		ingestionService.ingest();

		log.info("END IngestionSchedular.scheduleIngest()");
	}
}
