package com.delta.pcpingestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of PCP INGESTION Service Scheduler
 * 
 * @author ca94197
 * 
 * @since 1.0
 */
@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PCPIngestionServiceSchedular {

	@Autowired
	private PCPIngestionService pcpIngestionService;

	@Value("${pcp.ingestion.service.isPublishSingleClaim}")
	private Boolean isPublishSingleClaim = Boolean.FALSE;

	@Value("${pcp.ingestion.service.state}")
	private String state;

	@Value("${pcp.ingestion.service.isUsedTibco}")
	private Boolean isUsedTibco = Boolean.TRUE;

	@Scheduled(initialDelayString = "${job.pcp.contract.initial.delay}", fixedRateString = "${job.pcp.contract.fixed.delay}")
	@MethodExecutionTime
	public void scheduleToCreatedPCPContractFixedRateTask() {
		log.info("Schedular Call to create pcp contract.....");
		if (isUsedTibco) {
			pcpIngestionService.createPCPContract();
		} else {
			// Call Metavence to create Contract Records
			pcpIngestionService.createContractBymetavance(state);
		}
	}

	@Scheduled(initialDelayString = "${job.post.contract.tocalculation.initial.delay}", fixedRateString = "${job.post.contract.tocalculation.fixed.delay}")
	@MethodExecutionTime
	public void schedulePostContractDataOnPCPCalculationFixedRateTask() {
		log.info("Schedular Call for Posting Data on PCP calculation.....");
		if (isPublishSingleClaim) {
			pcpIngestionService.publishSingleClaimToPcpCalculationService();
		} else {
			pcpIngestionService.publishClaimsToPcpCalculationService();
		}
	}

	public void enableDisbaleTibcoServiceCall(Boolean isUsedTibco) {
		log.info(isUsedTibco == true ? "Enabled Tibco Service Call" : "Disabled Tibco Service Call");
		this.isUsedTibco = isUsedTibco;
	}
}
