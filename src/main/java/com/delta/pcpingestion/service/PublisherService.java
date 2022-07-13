package com.delta.pcpingestion.service;

import java.util.List;
import java.util.concurrent.ExecutorService;


import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.enums.State;
import com.delta.pcpingestion.interservice.PcpCalculationServiceClient;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.delta.pcpingestion.mapper.Mapper;
import com.delta.pcpingestion.repo.ContractDAO;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Component
public class PublisherService {

	@Autowired
	private ContractDAO contractDAO;

	@Autowired
	private PcpCalculationServiceClient pcpCalculationClient;

	//@Value("${pcp.ingestion.service.isUsedTibco}")
	//private Boolean isUsedTibco = Boolean.TRUE;

	
	@Value("${pcp.ingestion.service.numOfDays:10}")
	private Integer numOfDays;

	@Qualifier("publisherExecutor")
	private ExecutorService executor;

	@Autowired
	private Mapper mapper;

	@MethodExecutionTime
	@Synchronized
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

		log.info("Start Publishing records for state {}", state);
		List<ContractEntity> contractClaims = contractDAO.findByPublishStatusAndStateCode(PublishStatus.STAGED.name(), state.name());

		publish(contractClaims);
		
		log.info("Start Publishing records for state {},count {}", state,contractClaims.size() );

		log.info("END PublisherService.publish()");
	}

	private void publish( List<ContractEntity> contractClaims) {
		log.info("START PublisherService.publish()");

		if (!contractClaims.isEmpty()) {
			executor.submit(() -> {
				// FIXME: Partition request
				contractClaims.stream().forEach(this::publish);
			});

		}
		log.info("END PublisherService.publish()");

	}

	private void publish(ContractEntity contract) { // multiple contracts?
		log.info("START PublisherService.publish()");
		List<MemberContractClaimRequest> requests = mapper.mapRequest(contract);
		if(CollectionUtils.isNotEmpty(requests)) {
			pcpCalculationClient.publish(requests);
			contract.setPublishStatus(PublishStatus.COMPLETED);
		} else {
			log.warn("Not publishing for contract {} ", contract);
			contract.setPublishStatus(PublishStatus.INVALID_PROVIDER);
		}	
		contractDAO.save(contract);
		log.info("END PublisherService.publish()");
	}

	//public void enableDisbaleTibcoServiceCall(Boolean isUsedTibco) {
	//	log.info(isUsedTibco == true ? "Enabled Tibco Service Call" : "Disabled Tibco Service Call");
		//this.isUsedTibco = isUsedTibco;
	//}
}
