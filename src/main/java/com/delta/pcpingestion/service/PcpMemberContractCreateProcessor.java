package com.delta.pcpingestion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.dto.Contract;
import com.delta.pcpingestion.dto.Enrollee;
import com.delta.pcpingestion.dto.Member;
import com.delta.pcpingestion.entity.Claim;
import com.delta.pcpingestion.entity.PCPIngestionActivity;
import com.delta.pcpingestion.entity.PCPMemberContract;
import com.delta.pcpingestion.repo.ContractRepository;
import com.delta.pcpingestion.repo.PCPIngestionActivityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class PcpMemberContractCreateProcessor {

	@Autowired
	private TibcoClient tibcoClient;

	@Autowired
	private ContractRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PCPIngestionActivityRepository pcpIngestionActivityRepository;

	private String tibcoQueryStr = "{'pcpMembersRequest':'{\"states\":[${state}],\"numofdays\":${numofdays},\"receiveddate\":\"${receiveddate}\",\"pagenum\":${pagenum}}'}";

	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yy");

	public void createProcessorByState(State state, LocalDate cutOffDate, Integer numOfDays) {
		long startTime = System.currentTimeMillis();
		Map<String, String> params = new HashMap<>();
		List<PCPMemberContract> contracts = null;
		LocalDate processDate = LocalDate.now();
		log.info("Start process for [" + state + "] for cutOffDate[" + cutOffDate + "]");
		PCPIngestionActivity activity = new PCPIngestionActivity();
		while (cutOffDate.isBefore(processDate)) {
			params.put("state", "\"" + state.toString() + "\"");
			params.put("numofdays", numOfDays.toString());
			params.put("receiveddate", processDate.format(df).toString());
			contracts = createPcpMemberContract(state, params);
			processDate = processDate.minusDays(numOfDays);
			logContractActivityReceivedFromTibco(state, activity, contracts);
		}
		long endTime = System.currentTimeMillis();
		long seconds = TimeUnit.MILLISECONDS.toSeconds((endTime - startTime));
		log.info(" Thread Name : {} taken to complete process : {} second[s]", Thread.currentThread().getName(),
				seconds);
		if (activity != null && activity.getState() != null) {
			activity.setTibcoDataStagePeriod(seconds);
			pcpIngestionActivityRepository.save(activity);
		}
	}

	private void logContractActivityReceivedFromTibco(State state, PCPIngestionActivity activity,
			List<PCPMemberContract> contracts) {
		if (contracts != null && !contracts.isEmpty()) {
			int numOfContract = activity.getNumOfContract() + contracts.size();
			int numOfClaim = activity.getNumOfClaims() + totalClaimInContract(contracts);
			activity.setNumOfContract(numOfContract);
			activity.setNumOfClaims(numOfClaim);
			activity.setNumClaimSendToCalculation(0);
			activity.setState(state.toString());
		}
	}

	private List<PCPMemberContract> createPcpMemberContract(State state, Map<String, String> params) {
		int pagenum = 0;
		Boolean isMorerecods = Boolean.TRUE;
		List<PCPMemberContract> contracts = new ArrayList<>();
		while (isMorerecods) {
			Map<String, Integer> pageNumMap = new HashMap<>();
			String tibcoQueryStrRequest = StrSubstitutor.replace(tibcoQueryStr, params);
			pageNumMap.put("pagenum", pagenum);
			String paginatedtibcoQueryStr = StrSubstitutor.replace(tibcoQueryStrRequest, pageNumMap);
			log.info("Member call for tibco {}", paginatedtibcoQueryStr);
			ResponseEntity<Member> members = tibcoClient.fetchPcpmemberFromTibco(paginatedtibcoQueryStr);
			log.debug("Member Receive {}", members);
			if (null != members && null != members.getBody() && null != members.getBody().getPcpMembers()) {
				List<PCPMemberContract> contract = buildPcpMemberContract(
						members.getBody().getPcpMembers().getContracts());
				if (contract.size() > 0) {
					List<PCPMemberContract> savedContract = repository.saveAll(contract);
					log.info("Total " + savedContract.size() + " Contract is staged...");
					contracts.addAll(savedContract);
				} else {
					log.info("There is no contract to save..");
				}
				pagenum = pagenum + 1;
			} else {
				isMorerecods = Boolean.FALSE;
			}
		}
		return contracts;
	}

	private List<PCPMemberContract> buildPcpMemberContract(final List<Contract> contracts) {
		log.debug("buildPcpContract - Start Bulding list of contract from Tibco response...");
		List<PCPMemberContract> memberContract = contracts.stream() //
				.filter(contract -> isMemberContrctNullOrUpdatedLastWeek(contract.getContractID()))
				.map(contract -> PCPMemberContract.builder() //
						.contractID(contract.getContractID()) //
						.memberId(listOfEnrollMembers(contract.getEnrollees())) //
						.mtvPersonID(listOfEnrollMTVPersonID(contract.getEnrollees())) //
						.contract(convertIntoString(contract)) //
						.numberOfEnrollee(contract.getEnrollees().size()) //
						.claim(listOfEnrollClaims(contract.getEnrollees())) //
						.status(STATUS.STAGED) //
						.numOfAttempt(0) //
						.build())
				.collect(Collectors.toList());
		log.debug("buildPcpContractcontract Ends contract are is {}", memberContract);
		return memberContract;
	}

	private boolean isMemberContrctNullOrUpdatedLastWeek(String contractId) {
		PCPMemberContract contract = repository.findByContractID(contractId);
		if (contract == null) {
			return true;
		}
		LocalDate lastUpdateDate = contract.getLastUpdatedDate().toLocalDateTime().toLocalDate();
		LocalDate date = lastUpdateDate.plusDays(7);
		return (contract != null && (date.isBefore(LocalDate.now()))) ? true : false;
	}

	private Set<String> listOfEnrollMTVPersonID(List<Enrollee> enrolles) {
		return enrolles.stream().map(enrollee -> enrollee.getMtvPersonID()).distinct().collect(Collectors.toSet());
	}

	private Set<String> listOfEnrollMembers(List<Enrollee> enrolles) {
		return enrolles.stream().map(enrollee -> enrollee.getMemberId()).distinct().collect(Collectors.toSet());
	}

	private String convertIntoString(final Contract contract) {
		String contractStr = null;
		try {
			contractStr = objectMapper.writeValueAsString(contract);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Error while create contract Json [" + e.getMessage() + "]");
		}
		return contractStr;
	}

	private Set<Claim> listOfEnrollClaims(List<Enrollee> enrolles) {
		Set<Claim> claims = new HashSet<>();
		enrolles.stream().forEach(enrollee -> {
			enrollee.getClaims().stream().forEach(claim -> {
				Claim claimObj = Claim.builder(). //
				claimId(claim.getClaimId()) //
						.billingProviderId(claim.getBillingProviderId()) //
						.billProviderSpeciality(claim.getBillProviderSpeciality()) //
						.receivedDate(claim.getReceivedDate()) //
						.resolvedDate(claim.getResolvedDate()) //
						.encounterFlag(claim.getEmergencyFlag()) //
						.stateCode(claim.getStateCode()) //
						.claimStatus(claim.getClaimStatus()) //
						.emergencyFlag(claim.getEmergencyFlag()) //
						.securityGroupId(claim.getSecurityGroupId()) //
						.serviceNumber(claim.getServiceNumber()) //
						.build();
				claims.add(claimObj);
			});
		});

		return claims;
	}

	private Integer totalClaimInContract(List<PCPMemberContract> contracts) {
		Integer claimCount = contracts.stream().map(c -> c.getClaim() == null ? 0 : c.getClaim().size()).reduce(0,
				Integer::sum);
		return claimCount;

	}
}
