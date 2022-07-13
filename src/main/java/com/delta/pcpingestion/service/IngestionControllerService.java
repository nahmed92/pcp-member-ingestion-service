package com.delta.pcpingestion.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	private PCPConfigServiceClient configClient;

	@Value("${pcp.ingestion.service.num.of.days:10}")
	private Integer numOfDays;
	
	@Value("${service.nodes}")
	private String serviceNodes;

	private String[] serviceInstances; 
	
	@PostConstruct
	public void init() {
		log.info("START IngestionControllerService.init()");
		
		serviceInstances=serviceNodes.split(",");
		
		log.info("END IngestionControllerService.init()");
	}

	@Transactional
	public void populateControl() {
		log.info("START IngestionControllerService.populateControl()");

		String lookbackDays = configClient.claimLookBackDays();
		LocalDate cutOffDate = LocalDate.now().minusDays(Integer.parseInt(lookbackDays));

		log.info("lookbackDays {}, cutOffDate {}", lookbackDays, cutOffDate);

		// FIXME: check if pending records.
		List<IngestionControllerEntity> entities = generateEntities(cutOffDate, numOfDays);

		repo.saveAll(entities);

		log.info("persisted {} entities ", entities.size());

		log.info("END IngestionControllerService.populateControl()");

	}

	private List<IngestionControllerEntity> generateEntities(LocalDate cutOffDate, Integer numOfDays) {
		List<IngestionControllerEntity> entities = new ArrayList<>();
		String runId = UUID.randomUUID().toString();

		log.info("runId {}", runId);

		// FIXME: Group States
		
		State[] states =  State.values();
		for (int i=0;i<states.length;i++) {
			entities.add(generateEntity(runId, states[i].name(), cutOffDate, numOfDays,serviceInstances[i%serviceInstances.length]));
		}
		return entities;
	}

	private IngestionControllerEntity generateEntity(String runId, String states, LocalDate cutOffDate, int numOfDays,String serviceInstanceId) {
		return IngestionControllerEntity.builder().id(UUID.randomUUID().toString()).runId(runId)
				.runTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.createdAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.lastUpdatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.cutOffDate(Date.valueOf(cutOffDate))
				.serviceInstanceId(serviceInstanceId)
				.status(ControlStatus.CREATED).states(states).noOfContracts(0).noOfDays(numOfDays).build();
	}

}
