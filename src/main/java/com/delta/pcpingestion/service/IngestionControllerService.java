package com.delta.pcpingestion.service;

import static org.mockito.Mockito.validateMockitoUsage;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PCPConfigServiceClient;
import com.delta.pcpingestion.repo.IngestionControllerRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IngestionControllerService {

	@Autowired
	private IngestionControllerRepository repo;

	@Value("${enable.ingestion.controller}")
	private Boolean enableIngestionController;

	@Autowired
	private PCPConfigServiceClient configClient;

	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;

	/**
	 * Called from scheduler
	 */
	public void schedule() {
		log.info("START IngestionControllerService.schedule()");
		log.info("enableIngestionController:{}", enableIngestionController);
		if (enableIngestionController) {
			populateControl();
		}
		log.info("END IngestionControllerService.schedule()");
	}

	public void populateControl() {
		log.info("START IngestionControllerService.populateControl()");

		String lookbackDays = configClient.claimLookBackDays();
		LocalDate cutOffDate = LocalDate.now().minusDays(Integer.parseInt(lookbackDays));

		log.info("lookbackDays {}, cutOffDate {}", lookbackDays, cutOffDate);

		List<IngestionControllerEntity> entities = generateEntities(cutOffDate, numOfDays);

		repo.saveAll(entities);

		log.info("END IngestionControllerService.populateControl()");

	}

	private List<IngestionControllerEntity> generateEntities(LocalDate cutOffDate, Integer numOfDays) {
		List<IngestionControllerEntity> entities = new ArrayList<>();
		String runId = UUID.randomUUID().toString();
		
		log.info("runId {}",runId);
		
		// FIXME: Group States
		for (State state : State.values()) {
			entities.add(generateEntity(runId, state.name(),cutOffDate, numOfDays));
		}
		return entities;
	}

	private IngestionControllerEntity generateEntity(String runId, String states,LocalDate cutOffDate,int numOfDays) {
		return IngestionControllerEntity.builder().id(UUID.randomUUID().toString()).runId(runId)
				.runTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.createdAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.lastUpdatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.cutOffDate(Date.valueOf(cutOffDate))
				.status(ControlStatus.CREATED).states(states).noOfContracts(0).noOfDays(numOfDays).build();
	}

}
