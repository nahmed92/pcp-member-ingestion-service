package com.delta.pcpingestion.scheduler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.service.IngestionControllerService;
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
public class IngestionControllerScheduler {

	@Autowired
	private IngestionControllerService service;

	@Value("${controller.allowed.instance.id}")
	private String controllerAllowedInstanceId;
	
	@Value("${service.instance.id}")
	private String serviceInstanceId;
	
	@Value("${enable.ingestion.controller}")
	private Boolean enableIngestionController;
	

	//@Scheduled(cron = "0 0 * * * *")
	//@Scheduled(cron = "${scheduling.job.ingestion.controller.cron}")
	@Scheduled(initialDelayString = "${scheduling.job.ingestion.controller.delay}", fixedDelayString = "${scheduling.job.ingestion.controller.delay}")
	@MethodExecutionTime
	public void schedule() {
		log.info("START IngestionColtrollerScheduler.ingest()");

		if (StringUtils.equals(serviceInstanceId, controllerAllowedInstanceId)  && enableIngestionController) {
			service.populateControl();
		} else {
			log.info("Not scheduling, as serviceInstanceId{}, controllerAllowedInstanceId {}, enableIngestionController {}",
					serviceInstanceId, controllerAllowedInstanceId,enableIngestionController);
		}

		log.info("END IngestionColtrollerScheduler.ingest()");
	}
}
