package com.delta.pcpingestion.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.client.MessageResponse;
import com.delta.pcpingestion.client.PCPConfigServiceClient;
import com.delta.pcpingestion.client.PcpCalculationServiceClient;
import com.delta.pcpingestion.client.ValidateProviderRequest;
import com.delta.pcpingestion.client.ValidateProviderResponse;
import com.delta.pcpingestion.dto.Contract;
import com.delta.pcpingestion.entity.PCPMemberContract;
import com.delta.pcpingestion.mtv.entities.MbrProvNtwkAssn;
import com.delta.pcpingestion.mtv.repo.MetavanceRepo;
import com.delta.pcpingestion.repo.ContractRepository;
import com.delta.pcpingestion.repo.PCPIngestionActivityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of PCP INGESTION Service
 * 
 * @author ca94197
 * 
 * @since 1.0
 */
@Service
@Slf4j
@Component
public class PCPIngestionService {

	@Autowired
	private ContractRepository repository;

	@Autowired
	private PCPIngestionActivityRepository pcpIngestionActivityRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PcpCalculationServiceClient pcpCalculationClient;

	@Autowired
	private PCPConfigServiceClient configClient;

	@Autowired
	private PcpMemberContractCreateProcessor pcpMemberContractCreateProcessor;

	@Autowired
	private MetavanceRepo metavanceRepo;

	@Value("${metavance.pcp.ingestion.service.last_maintanence_time_stamp:null}")
	private String last_maintanence_time_stamp = null;

	@Value("${pcp.ingestion.process.workers.count:8}")
	private Integer pcpIngestionProcessWorkersCount;
	
	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;

	public List<PCPMemberContract> getAllContract() {
		return (List<PCPMemberContract>) repository.findAll();
	}

	public void createPCPContract() {
		log.info("createPCPContract - Tibco Request to create pcp member contract started");
		ExecutorService executor = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount);
		String lookbackDays = configClient.providerLookBackDays();
		LocalDate cutOffDate = LocalDate.now().minusDays(Integer.parseInt(lookbackDays));
		for (State state : State.values()) {
			executor.submit(() -> {
				pcpMemberContractCreateProcessor.createProcessorByState(state, cutOffDate, numOfDays);
			});
		}
		executor.shutdown();
		log.info("createPCPContract - PCP Ingestion Service Process Complete...");
	}

	public void publishSingleClaimToPcpCalculationService() {
		ExecutorService executorforClaim = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount);
		for (State state : State.values()) {
			executorforClaim.submit(() -> {
				List<PCPMemberContract> contractClaims = repository.findByStatusAndStateCode(STATUS.STAGED.ordinal(),
						state.toString());
				log.info("Start Publish {} contract records for state {}....", contractClaims.size(), state);
				if (!contractClaims.isEmpty()) {
					contractClaims.stream().forEach(contract -> {
						contract.getClaim().stream().forEach(claim -> {
							ValidateProviderRequest request = ValidateProviderRequest.builder() //
									.claimId(claim.getClaimId()) //
									.contractId(contract.getContractID()) //
									.memberId(contract.getMemberId().iterator().next()) //
									.providerId(claim.getBillingProviderId()) //
									.operatorId("PCP-ING") //
									.state(claim.getStateCode()) //
									.build();
							ResponseEntity<ValidateProviderResponse> response = pcpCalculationClient
									.publishAssignMemberPCP(request);
						});
						contract.setStatus(STATUS.COMPLETED);
						repository.save(contract);
					});
					pcpIngestionActivityRepository.updatePcpIngestionActivityForNumberOfClaim(contractClaims.size(),
							state.toString());
					log.info("Publish single {} Contract to PCP-Calculation-service for state {}....",
							contractClaims.size(), state);
				}
			});
		}
		executorforClaim.shutdown();
	}

	public void publishClaimsToPcpCalculationService() {
		ExecutorService executorforClaims = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount);
		for (State state : State.values()) {
			log.info("Start Publish records for state {}....", state);
			List<PCPMemberContract> contractClaims = repository.findByStatusAndStateCode(STATUS.STAGED.ordinal(),
					state.toString());
			executorforClaims.submit(() -> {
				if (!contractClaims.isEmpty()) {
					List<ValidateProviderRequest> request = new ArrayList<>();
					contractClaims.stream().forEach(contract -> {
						contract.getClaim().stream().forEach(claim -> {
							ValidateProviderRequest validateProviderRequest = ValidateProviderRequest.builder()
									.claimId(claim.getClaimId()) //
									.contractId(contract.getContractID()) //
									.memberId(contract.getMemberId().iterator().next()) //
									.providerId(claim.getBillingProviderId()) //
									.operatorId("PCP-ING") //
									.state(claim.getStateCode()) //
									.build(); //
							request.add(validateProviderRequest);
						});
						ResponseEntity<MessageResponse> response = pcpCalculationClient
								.publishAssignMembersPCP(request);
						contract.setStatus(STATUS.COMPLETED);
						repository.save(contract);
					});
					pcpIngestionActivityRepository.updatePcpIngestionActivityForNumberOfClaim(contractClaims.size(),
							state.toString());
					log.info("Publish {} Contract to PCP-Calculation-service for state {}....", contractClaims.size(),
							state);
				}
			});
		}
		executorforClaims.shutdown();
	}

	public void createContractBymetavance(String state) {
		List<MbrProvNtwkAssn> mbrProvNtwkAssn;
		if (last_maintanence_time_stamp.equals("null")) {
			mbrProvNtwkAssn = metavanceRepo.findAllFirstHunderedMTVMemberContractEntity(state);
			log.info("Total number of records fetch from first Query " + mbrProvNtwkAssn.size());
			updateLastMaintanenceDate(mbrProvNtwkAssn);
		} else {
			mbrProvNtwkAssn = metavanceRepo.findAllAfterFirstHunderedMTVMemberContractEntity(state,
					Timestamp.valueOf(last_maintanence_time_stamp));
			log.info("Total number of records fetch from second Query " + mbrProvNtwkAssn.size());
			updateLastMaintanenceDate(mbrProvNtwkAssn);
		}
		// @Todo List of mtv_Person_ID is empty
		// they are un-availbale in current query
		List<PCPMemberContract> memberContract = mbrProvNtwkAssn.stream()
				.map(contract -> PCPMemberContract.builder().contractID(contract.getContractID())
						.memberId(listsOfMembers(mbrProvNtwkAssn, contract.getContractID()))
						.mtvPersonID(new HashSet<>())
						.contract(convertIntoString(createContractFromMetavenceRecords(contract))).numberOfEnrollee(3)
						.status(STATUS.STAGED).numOfAttempt(0).build())
				.collect(Collectors.toList());
		if (memberContract.size() > 0) {
			log.info("Start Staging Records..");
			Date start = new Date();
			List<PCPMemberContract> savedContract = (List<PCPMemberContract>) repository.saveAll(memberContract);
			log.info("Total " + savedContract.size() + " Contract is staged at time....["
					+ calculateTime(start, new Date()) + "]");
		} else {
			log.info("There is no contract to save..");
		}
	}

	private void updateLastMaintanenceDate(List<MbrProvNtwkAssn> mbrProvNtwkAssn) {
		if (mbrProvNtwkAssn.size() > 0) {
			last_maintanence_time_stamp = mbrProvNtwkAssn.get(mbrProvNtwkAssn.size() - 1).getLast_maint_ts().toString();
		}
	}

	private Contract createContractFromMetavenceRecords(MbrProvNtwkAssn mbrProvNtwkAssn) {
		Contract contract = new Contract();
		contract.setContractID(mbrProvNtwkAssn.getContractID().trim());
		contract.setDivisionNumber("test-123");
		contract.setGroupNumber("test-123");
		contract.setEnrollees(new ArrayList<>());
		return contract;
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

	private Set<String> listsOfMembers(List<MbrProvNtwkAssn> mbrProvNtwkAssns, String contractId) {
		return mbrProvNtwkAssns.stream()
				.filter(mbrProvNtwkAssn -> mbrProvNtwkAssn.getContractID().trim().equals(contractId.trim()))
				.map(mbrProvNtwkAssn -> mbrProvNtwkAssn.getMemberId()).collect(Collectors.toSet());
	}

	private String calculateTime(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		String TimeTaken = String.format("[%s] hours : [%s] mins : [%s] secs", TimeUnit.MILLISECONDS.toHours(diff),
				TimeUnit.MILLISECONDS.toMinutes(diff), TimeUnit.MILLISECONDS.toSeconds(diff));
		return TimeTaken;
	}
}
