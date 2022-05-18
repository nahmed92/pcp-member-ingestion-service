package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.tibco.TibcoClient;
import com.delta.pcpingestion.repo.ContractRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class ContractIngester {

	@Autowired
	private TibcoClient tibcoClient;

	@Autowired
	private ContractRepository repository;

	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yy");

	@MethodExecutionTime
	public void ingestByState(State state, LocalDate cutOffDate, Integer numOfDays) {
		log.info("START ContractIngester.ingestByState()");
		Map<String, String> params = new HashMap<>();
		LocalDate processDate = LocalDate.now();
		log.info("Start process for [" + state + "] for cutOffDate[" + cutOffDate + "]");
		while (cutOffDate.isBefore(processDate)) {
			params.put("state", "\"" + state.toString() + "\"");
			params.put("numofdays", numOfDays.toString());
			params.put("receiveddate", processDate.format(df).toString());
			ingestAndPersist(state, params);
			processDate = processDate.minusDays(numOfDays);
		}
		log.info("END ContractIngester.ingestByState()");
	}

	private void ingestAndPersist(State state, Map<String, String> params) {
		log.info("START ContractIngester.ingestAndPersist()");
		int pagenum = 0;
		Boolean isMorerecods = Boolean.TRUE;
		while (isMorerecods) {
			params.put("pagenum", "" + pagenum);
			List<ContractEntity> contractEntities = tibcoClient.fetchContracts(params);
			log.debug("Member Receive {}", contractEntities);
			if (CollectionUtils.isNotEmpty(contractEntities)) {
				List<ContractEntity> entitiesToSave = contractEntities.stream()
						.filter(i -> isAllowedToSave(i)).collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(entitiesToSave)) {
					repository.saveAll(entitiesToSave);
				}
				log.info("Contract is staged... {} ", entitiesToSave.size());

			} else {
				log.info("There is no contract to save..");
				isMorerecods = Boolean.FALSE;
			}
			pagenum = pagenum + 1;
		}
		log.info("END ContractIngester.ingestAndPersist()");
	}

	// Setting Id for contract
	private boolean isAllowedToSave(ContractEntity contract) {
		boolean returnValue = false;
		Optional<ContractEntity> optionalContractEntity = repository.findByContractId(contract.getContractId());
		if (optionalContractEntity.isEmpty()) {
			contract.setId(UUID.randomUUID().toString());
			returnValue = true;
		} else {
			LocalDate lastUpdateDate = optionalContractEntity.get().getLastUpdatedAt().toLocalDateTime().toLocalDate();
			// TODO : externalize this days property
			LocalDate date = lastUpdateDate.plusDays(7);
			returnValue = (date.isBefore(LocalDate.now())) ? true : false;
			if(returnValue) {
				contract.setId(optionalContractEntity.get().getId());
			}
		}
		log.info("for contractId {}, isAllowdTosave:{} ", contract.getContractId(), returnValue);
		return returnValue;
	}

}
