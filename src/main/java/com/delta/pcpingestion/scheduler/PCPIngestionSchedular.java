package com.delta.pcpingestion.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.service.PCPIngestionService;
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
public class PCPIngestionSchedular {

	@Autowired
	private PCPIngestionService pcpIngestionService;

	@Scheduled(initialDelayString = "${job.pcp.contract.initial.delay}", fixedRateString = "${job.pcp.contract.fixed.delay}")
	@MethodExecutionTime
	public void scheduleToCreatedPCPContractFixedRateTask() {
		log.info("START PCPIngestionSchedular.scheduleToCreatedPCPContractFixedRateTask()");
	
		pcpIngestionService.ingest();

		log.info("END PCPIngestionSchedular.scheduleToCreatedPCPContractFixedRateTask()");
	}
}
