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

	@Scheduled(cron = "0 0 * * * *")
	@MethodExecutionTime
	public void schedule() {
		log.info("START IngestionColtrollerScheduler.ingest()");

		if (StringUtils.equals(serviceInstanceId, controllerAllowedInstanceId)) {
			service.schedule();
		} else {
			log.info("Not scheduling, as serviceInstanceId{}, and controllerAllowedInstanceId {} are not same",
					serviceInstanceId, controllerAllowedInstanceId);
		}

		log.info("END IngestionColtrollerScheduler.ingest()");
	}
}
