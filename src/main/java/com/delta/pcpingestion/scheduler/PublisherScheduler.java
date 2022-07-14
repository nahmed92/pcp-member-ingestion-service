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

//	@Scheduled(cron = "0 5 * * * *")
	//@Scheduled(cron = "${scheduling.job.publisher.cron}")
	@Scheduled(initialDelayString = "${scheduling.job.publisher.delay}", fixedDelayString = "${scheduling.job.publisher.delay}")
	@MethodExecutionTime
	public void publish() {
		log.info("START PublisherScheduler.publish()");
		publisherService.publish();
		log.info("END PublisherScheduler.publish()");
	}
}
