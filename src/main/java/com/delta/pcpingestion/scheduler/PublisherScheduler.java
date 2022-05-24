package com.delta.pcpingestion.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.service.PublisherService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublisherScheduler {

	@Autowired
	private PublisherService publisherService;

	@Scheduled(cron = "${job.publisher.cron.scheduler:*/30 * * * *}")
	@MethodExecutionTime
	public void schedulePostContractDataOnPCPCalculationFixedRateTask() {
		log.info("START PublisherScheduler.schedulePostContractDataOnPCPCalculationFixedRateTask()");
		publisherService.publish();
		log.info("END PublisherScheduler.schedulePostContractDataOnPCPCalculationFixedRateTask()");
	}
}
