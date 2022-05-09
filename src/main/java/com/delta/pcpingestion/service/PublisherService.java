package com.delta.pcpingestion.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.client.MessageResponse;
import com.delta.pcpingestion.client.PcpCalculationServiceClient;
import com.delta.pcpingestion.client.ValidateProviderRequest;
import com.delta.pcpingestion.entity.Claim;
import com.delta.pcpingestion.entity.PCPMemberContract;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.repo.ContractRepository;
import com.delta.pcpingestion.repo.PCPIngestionActivityRepository;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Component
public class PublisherService {

	@Autowired
	private ContractRepository repository;

	@Autowired
	private PCPIngestionActivityRepository pcpIngestionActivityRepository;

	@Autowired
	private PcpCalculationServiceClient pcpCalculationClient;

	@Value("${pcp.ingestion.service.isUsedTibco}")
	private Boolean isUsedTibco = Boolean.TRUE;

	@Value("${pcp.ingestion.process.workers.count:5}")
	private Integer pcpIngestionProcessWorkersCount;

	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;

	private ExecutorService executor;

	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(pcpIngestionProcessWorkersCount);
	}

	@MethodExecutionTime
	public void publish() {
		log.info("START PublisherService.publish()");

		for (State state : State.values()) {
			publish(state);
		}

		log.info("END PublisherService.publish()");
	}

	@MethodExecutionTime
	private void publish(State state) {
		log.info("START PublisherService.publish()");

		log.info("Start Publish records for state {}", state);
		List<PCPMemberContract> contractClaims = repository.findByStatusAndStateCode(PublishStatus.STAGED, state);

		publish(state, contractClaims);

		log.info("END PublisherService.publish()");
	}

	private void publish(State state, List<PCPMemberContract> contractClaims) {
		log.info("START PublisherService.publish()");

		if (!contractClaims.isEmpty()) {
			executor.submit(() -> {
				// FIXME: Partition request
				contractClaims.stream().forEach(contract -> publish(contract));
				pcpIngestionActivityRepository.updatePcpIngestionActivityForNumberOfClaim(contractClaims.size(), state);
				log.info("Publish {} Contract to PCP-Calculation-service for state {}....", contractClaims.size(),
						state);

			});

		}
		log.info("END PublisherService.publish()");

	}

	private void publish(PCPMemberContract contract) {
		log.info("START PublisherService.publish()");
		List<ValidateProviderRequest> requests = map(contract);
		ResponseEntity<MessageResponse> response = pcpCalculationClient.publishAssignMembersPCP(requests);
		contract.setStatus(PublishStatus.COMPLETED);
		repository.save(contract);
		log.info("END PublisherService.publish()");
	}

	private List<ValidateProviderRequest> map(PCPMemberContract contract) {
		return contract.getClaim().stream().map(i -> map(contract, i)).collect(Collectors.toList());
	}

	private ValidateProviderRequest map(PCPMemberContract contract, Claim claim) {
		ValidateProviderRequest validateProviderRequest = ValidateProviderRequest.builder().claimId(claim.getClaimId()) //
				.contractId(contract.getContractID()) //
				.memberId(contract.getMemberId().iterator().next()) //
				.providerId(claim.getBillingProviderId()) //
				.operatorId("PCP-ING") //
				.state(claim.getStateCode()) //
				.build(); //
		return validateProviderRequest;
	}

	public void enableDisbaleTibcoServiceCall(Boolean isUsedTibco) {
		log.info(isUsedTibco == true ? "Enabled Tibco Service Call" : "Disabled Tibco Service Call");
		this.isUsedTibco = isUsedTibco;
	}
}
