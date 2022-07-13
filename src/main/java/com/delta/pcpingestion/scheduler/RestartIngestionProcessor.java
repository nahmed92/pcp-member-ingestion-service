package com.delta.pcpingestion.scheduler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.service.IngestionService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestartIngestionProcessor {

	@Autowired
	private IngestionService ingestionService;
	
	@PostConstruct
	public void init() {
		ingestInProgress();
	}

	@MethodExecutionTime
	public void ingestInProgress() {
		log.info("START RestartIngestionProcessor.ingestInProgress()");

		ingestionService.ingestInProgress();

		log.info("END RestartIngestionProcessor.ingestInProgress()");
	}

}
