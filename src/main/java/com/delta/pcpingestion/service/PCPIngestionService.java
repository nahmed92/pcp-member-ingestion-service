package com.delta.pcpingestion.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.client.PcpCalculationServiceClient;
import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.dto.Contract;
import com.delta.pcpingestion.dto.Enrollee;
import com.delta.pcpingestion.dto.Member;
import com.delta.pcpingestion.entity.PCPMemberContractEntity;
import com.delta.pcpingestion.mtv.entities.MbrProvNtwkAssn;
import com.delta.pcpingestion.mtv.repo.MetavanceRepo;
import com.delta.pcpingestion.repo.ContractRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
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
	private ObjectMapper objectMapper;

	@Autowired
	private PcpCalculationServiceClient pcpCalculationClient;

	@Autowired
	private TibcoClient tibcoClient;

	@Autowired
	private MetavanceRepo metavanceRepo;

	private LocalDate lastprocessedDate = null;

	private List<String> contractCreatedForTodate = new ArrayList<>();

    @Value("${metavance.pcp.ingestion.service.last_maintanence_time_stamp:null}")
	private String last_maintanence_time_stamp = null;

	@Value("${pcp.ingestion.service.tibcoQueryStr}")
	private String tibcoQueryStr;

	@Value("${pcp.ingestion.service.isUsedTibco}")
	private Boolean isUsedTibco = Boolean.TRUE;

	@Value("${pcp.ingestion.service.state}")
	private String state;

	 @Scheduled(initialDelayString = "${job.pcp.contract.initial.delay}", 
			 fixedRateString = "${job.pcp.contract.fixed.delay}")
	 @MethodExecutionTime
	public void scheduleToCreatedPCPContractFixedRateTask() {
		log.info("Schedular Call to create pcp contract for Every 5 Second..");
		if (isUsedTibco) {
			// Call Tibco Service to Create Contract Records
			createPCPContract(tibcoQueryStr);
		} else {
			// Call Metavence to create Contract Records
			createContractBymetavance(state);
		}
	}

	@Scheduled(initialDelayString = "${job.post.contract.tocalculation.initial.delay}", 
			fixedRateString = "${job.post.contract.tocalculation.fixed.delay}")
	@MethodExecutionTime
	public void schedulePostContractDataOnPCPCalculationFixedRateTask() {
		log.info("Schedular Call for Posting Data on PCP calculation Every 20 Second..");
		callPcpCalculationToPublishContract();
	}

	public List<PCPMemberContractEntity> getAllContract() {
		return repository.findAll();
	}

	public void enableDisbaleTibcoServiceCall(Boolean isUsedTibco) {
		log.info(isUsedTibco == true ? "Enabled Tibco Service Call" : "Disabled Tibco Service Call");
		this.isUsedTibco = isUsedTibco;
	}

	public void callPcpCalculationToPublishContract() {
		List<PCPMemberContractEntity> contracts = repository.findByStatus(STATUS.STAGED);
        log.info("Publish Contract to PCP-Calculation-service....");
        pcpCalculationClient.publishContractToPcpCalcuationService(contracts);
		if (contracts.size() > 0) {
			contracts.forEach(contract -> {
				contract.setStatus(STATUS.COMPLETED);
			});
			repository.saveAll(contracts);
		}
	}    

	public ResponseEntity<Member> fetchPcpmemberFromTibco(String tibcoQueryStr) {
		return tibcoClient.fetchPcpmemberFromTibco(tibcoQueryStr);
	}

	public void createPCPContract(final String pcpMembersRequestStr) {
		log.info("createPCPContract - Tibco Request to create pcp member contract started");
		ResponseEntity<Member> members = fetchPcpmemberFromTibco(tibcoQueryStr);
		log.debug("Member Receive {}", members);
		if (members != null) {
			List<PCPMemberContractEntity> contract = buildPcpMemberContract(
					members.getBody().getPcpMembers().getContracts());
			if (contract.size() > 0) {
				List<PCPMemberContractEntity> savedContract = repository.saveAll(contract);
				log.info("Total " + savedContract.size() + " Contract is staged...");
				// sending to hold contractIds saved in current date
				this.contractSavedToDate(savedContract);
			} else {
				log.info("There is no contract to save..");
			}
		}
	}

	private void createContractBymetavance(String state) {
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
		// @Todo groupNumber,  divisionNumber , numberOfEnrollee is hard code
		// they are un-availbale in current query
		List<PCPMemberContractEntity> memberContract = mbrProvNtwkAssn.stream()
				.map(contract -> new PCPMemberContractEntity(contract.getContractID(),
						listsOfMembers(mbrProvNtwkAssn, contract.getContractID()), "123", "123",
						convertIntoString(createContractFromMetavenceRecords(contract)), 3, STATUS.STAGED, 0))
				.distinct().collect(Collectors.toList());
		if (memberContract.size() > 0) {
			log.info("Start Staging Records..");
			Date start = new Date();
			List<PCPMemberContractEntity> savedContract = repository.saveAll(memberContract);
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

	public List<PCPMemberContractEntity> buildPcpMemberContract(final List<Contract> contracts) {
		log.debug("buildPcpContract - Start Bulding list of contract from Tibco response...");
		List<PCPMemberContractEntity> memberContract = contracts.stream()
				.filter(contract -> !contractCreatedForTodate.contains(contract.getContractID()))
				.map(contract -> new PCPMemberContractEntity(contract.getContractID(),
						listOfEnrollMembers(contract.getEnrollees()), contract.getGroupNumber(),
						contract.getDivisionNumber(), convertIntoString(contract), contract.getEnrollees().size(),
						STATUS.STAGED, 0))
				.distinct().collect(Collectors.toList());
		log.debug("buildPcpContractcontract Ends contract are is {}", memberContract);
		return memberContract;
	}

	private void contractSavedToDate(List<PCPMemberContractEntity> savedContracts) {
		if (lastprocessedDate == null || lastprocessedDate.isBefore(LocalDate.now())) {
			this.contractCreatedForTodate = new ArrayList<>();
			lastprocessedDate = LocalDate.now();
		}
		savedContracts.forEach(contract -> {
			if (!this.contractCreatedForTodate.contains(contract.getContractID())) {
				this.contractCreatedForTodate.add(contract.getContractID());
			}
		});
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

	private List<String> listOfEnrollMembers(List<Enrollee> enrolles) {
		return enrolles.stream().map(enrollee -> enrollee.getMemberId()).distinct().collect(Collectors.toList());
	}

	private List<String> listsOfMembers(List<MbrProvNtwkAssn> mbrProvNtwkAssns, String contractId) {
		return mbrProvNtwkAssns.stream()
				.filter(mbrProvNtwkAssn -> mbrProvNtwkAssn.getContractID().trim().equals(contractId.trim()))
				.map(mbrProvNtwkAssn -> mbrProvNtwkAssn.getMemberId()).collect(Collectors.toList());
	}

	public String calculateTime(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		String TimeTaken = String.format("[%s] hours : [%s] mins : [%s] secs", TimeUnit.MILLISECONDS.toHours(diff),
				TimeUnit.MILLISECONDS.toMinutes(diff), TimeUnit.MILLISECONDS.toSeconds(diff));
		return TimeTaken;
	}
}
