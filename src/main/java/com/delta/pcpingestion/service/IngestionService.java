package com.delta.pcpingestion.service;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;
import com.delta.pcpingestion.repo.IngestionControllerRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.google.common.base.Optional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Component
public class IngestionService {

	@Autowired
	private ContractIngester contractIngester;

	@Autowired
	private IngestionControllerRepository repo;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	@Autowired
	@Qualifier("ingestionExecutor")
	private ExecutorService executor;

	@Async
	@MethodExecutionTime
	@Transactional
	public void ingest() {
		log.info("START PCPIngestionService.ingest()");

		// ingestFromTibco();
		ingestByControlStatus(ControlStatus.CREATED);

		log.info("END PCPIngestionService.ingest()");
	}

	@MethodExecutionTime
	@Transactional
	public void ingestInProgress() {
		log.info("START PCPIngestionService.ingestInProgress()");

		List<IngestionControllerEntity> entities = repo.findAllByStatusAndServiceInstanceId(ControlStatus.IN_PROGRESS,serviceInstanceId);

		log.info("Processing pending records {}",entities);
		
		entities.forEach(this::ingest);

		log.info("END PCPIngestionService.ingestInProgress()");
	}

	private void ingestByControlStatus(ControlStatus status) {
		log.info("START IngestionService.ingestByControlStatus()");

		// FIXME: loop while u get null results
		// FIXME: submit to executor
		boolean recordPresent = false;
		do {
			Optional<IngestionControllerEntity> entityOptional = readEntityAndUpdate(status);

			if (entityOptional.isPresent()) {
				recordPresent = true;
				IngestionControllerEntity entity = entityOptional.get();
				repo.save(entity);
				executor.submit(() -> {
					ingest(entity);
				});

			} else {
				recordPresent = false;
			}

		} while (recordPresent);

		log.info("END IngestionService.ingestByControlStatus()");

	}

	private Optional<IngestionControllerEntity> readEntityAndUpdate(ControlStatus status) {
		log.info("START IngestionService.readEntityAndUpdate()");
		log.info("Processing for {} ", status);
		Optional<IngestionControllerEntity> entityOptional = repo.findFirstByStatusAndServiceInstanceId(status,
				serviceInstanceId);
		if (entityOptional.isPresent()) {
			IngestionControllerEntity entity = entityOptional.get();
			entity.setStatus(ControlStatus.IN_PROGRESS);
			entity.setServiceInstanceId(serviceInstanceId);
			repo.save(entity);
		}
		log.info("END IngestionService.readEntityAndUpdate()");
		return entityOptional;
	}

	private void ingest(IngestionControllerEntity entity) {
		contractIngester.ingest(entity);
		entity.setStatus(ControlStatus.COMPLETED);
		repo.save(entity);
	}

}
