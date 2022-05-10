package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.State;
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
		log.info("START PcpMemberContractCreateProcessor.createProcessorByState()");
		long startTime = System.currentTimeMillis();
		Map<String, String> params = new HashMap<>();
		LocalDate processDate = LocalDate.now();
		log.info("Start process for [" + state + "] for cutOffDate[" + cutOffDate + "]");
		while (cutOffDate.isBefore(processDate)) {
			params.put("state", "\"" + state.toString() + "\"");
			params.put("numofdays", numOfDays.toString());
			params.put("receiveddate", processDate.format(df).toString());
			createPcpMemberContract(state, params);
			processDate = processDate.minusDays(numOfDays);
		}
		long endTime = System.currentTimeMillis();
		long seconds = TimeUnit.MILLISECONDS.toSeconds((endTime - startTime));
		log.info(" Thread Name : {} taken to complete process : {} second[s]", Thread.currentThread().getName(),
				seconds);

		log.info("END PcpMemberContractCreateProcessor.createProcessorByState()");
	}

	private void createPcpMemberContract(State state, Map<String, String> params) {
		int pagenum = 0;
		Boolean isMorerecods = Boolean.TRUE;
		while (isMorerecods) {
			params.put("pagenum", "" + pagenum);
			List<ContractEntity> contractEntities = tibcoClient.fetchContracts(params);
			log.debug("Member Receive {}", contractEntities);
			if (CollectionUtils.isNotEmpty(contractEntities)) {
				List<ContractEntity> entitiesToSave = contractEntities.stream()
						.filter(i -> isAllowedToSave(i.getContractId())).collect(Collectors.toList());

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
	}

	private boolean isAllowedToSave(String contractId) {
		Optional<ContractEntity> optionalContractEntity = repository.findByContractId(contractId);
		if (optionalContractEntity.isEmpty()) {
			return true;
		} else {
			LocalDate lastUpdateDate = optionalContractEntity.get().getLastUpdatedAt().toLocalDateTime().toLocalDate();
			LocalDate date = lastUpdateDate.plusDays(7);
			return (date.isBefore(LocalDate.now())) ? true : false;
		}

	}

}
