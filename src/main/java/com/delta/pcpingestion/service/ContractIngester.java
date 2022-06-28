package com.delta.pcpingestion.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.repo.IngestionControllerRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.mapper.Mapper;
import com.delta.pcpingestion.repo.ContractRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope("prototype")
@Slf4j
public class ContractIngester {

	@Autowired
	private TibcoClient tibcoClient;

	@Autowired
	private ContractRepository repo;

	@Autowired
	private IngestionControllerRepository ingestionControllerRepo;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	@Autowired
	private Mapper mapper;

	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yy");

	@MethodExecutionTime
	@Transactional
	public void ingestByState(State state, LocalDate cutOffDate, Integer numOfDays) {
		log.info("START ContractIngester.ingestByState()");
		Map<String, String> params = new HashMap<>();
		LocalDate processDate = LocalDate.now();
		log.info("Start process for [" + state + "] for cutOffDate[" + cutOffDate + "]");
		Stopwatch stopwatch = Stopwatch.createStarted();
		while (cutOffDate.isBefore(processDate)) {
			params.put("state", "\"" + state.toString() + "\"");
			params.put("numofdays", numOfDays.toString());
			params.put("receiveddate", processDate.format(df).toString());
			ingestAndPersist(state, params);
			processDate = processDate.minusDays(numOfDays);
		}
		stopwatch.stop();
		log.info("Completed ingestion for state {},CutOffDate {},numOfDays {}, in {}  seconds ",state,cutOffDate,numOfDays, stopwatch.elapsed(TimeUnit.SECONDS));
		log.info("END ContractIngester.ingestByState()");
	}

	private void ingestAndPersist(State state, Map<String, String> params) {
		log.info("START ContractIngester.ingestAndPersist()");
		Stopwatch  stopwatch = Stopwatch.createStarted();
		int pagenum = 0;
		Boolean isMorerecods = Boolean.TRUE;
		int totalNumberOfRecords = 0;
		String runId = UUID.randomUUID().toString();
		while (isMorerecods) {
			params.put("pagenum", "" + pagenum);
			List<ContractEntity> contractEntities = tibcoClient.fetchContracts(params);
			log.debug("Member Receive {}", contractEntities);
			if (CollectionUtils.isNotEmpty(contractEntities)) {
				contractEntities.forEach(this::save);
				totalNumberOfRecords +=  contractEntities.size();
				saveIngestionControllerEntity(runId, state.toString(), totalNumberOfRecords);
			} else {
				log.info("There is no contract to save..");
				isMorerecods = Boolean.FALSE;
			}
			pagenum = pagenum + 1;
		}
		stopwatch.stop();
		log.info("Completed State {}, params {} , TotalNumberOfRecords {}, pages {} , completed in sec {} ",state,params,totalNumberOfRecords,pagenum,stopwatch.elapsed(TimeUnit.SECONDS));
		
		log.info("END ContractIngester.ingestAndPersist()");
	}

	// Setting Id for contract
	private void save(ContractEntity contract) {
		log.info("START ContractIngester.save()");

		Optional<ContractEntity> optionalContractEntity = repo.findByContractId(contract.getContractId());
		if (optionalContractEntity.isEmpty()) {
			contract.setId(UUID.randomUUID().toString());
			log.info("Saving contract {}", contract);
			repo.save(contract);
		} else {

			ContractEntity dbContract = optionalContractEntity.get();
			mergeAndSave(dbContract, contract);
		}

		log.info("END ContractIngester.save()");

	}

	private void mergeAndSave(ContractEntity dbContractEntity, ContractEntity contractEntity) {
		log.info("START ContractIngester.mergeAndSave()");

		ContractEntity mergedEntity = mapper.merge(dbContractEntity, contractEntity);

		repo.save(mergedEntity);
		/*
		 * LocalDate lastUpdateDate =
		 * dbContractEntity.getLastUpdatedAt().toLocalDateTime().toLocalDate(); // TODO
		 * : externalize this days property LocalDate date = lastUpdateDate.plusDays(7);
		 * boolean updateFlag = (date.isBefore(LocalDate.now())) ? true : false;
		 * if(updateFlag) {
		 * 
		 * }
		 */

		log.info("END ContractIngester.mergeAndSave()");
	}

	private void saveIngestionControllerEntity(String runId, String states, int numOfContracts) {
		IngestionControllerEntity ingestionControllerEntity = IngestionControllerEntity.builder()
				.id(UUID.randomUUID().toString())
				.runId(runId)
				.runTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Los_Angeles"))))
				.status(PublishStatus.CREATED.name())
				.states(states)
				.noOfContracts(numOfContracts)
				.serviceInstanceId(serviceInstanceId)
				.build();
		ingestionControllerRepo.save(ingestionControllerEntity);
	}
}
