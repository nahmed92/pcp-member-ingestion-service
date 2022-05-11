package com.delta.pcpingestion.mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.interservice.dto.MemberContractClaimRequest;
import com.delta.pcpingestion.interservice.tibco.dto.Claim;
import com.delta.pcpingestion.interservice.tibco.dto.Contract;
import com.delta.pcpingestion.interservice.tibco.dto.Enrollee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Mapper {

	@Autowired
	private ObjectMapper objectMapper;

	public List<ContractEntity> map(final List<Contract> contracts) {
		log.info("START Mapper.map()");
		List<ContractEntity> memberContract = contracts.stream().map(c -> map(c)).collect(Collectors.toList());
		log.info("END Mapper.map()");
		return memberContract;
	}

	private ContractEntity map(Contract contract) {
		ContractEntity entity = ContractEntity.builder() //
				.publishStatus(PublishStatus.STAGED) //
				.numOfRetries(0) //
				.build();

		Set<String> stateCodes = new HashSet<>();
		Set<String> mtvPersionIds = new HashSet<>();
		Set<String> claimIds = new HashSet<>();
        
		for (Enrollee enrollee : contract.getEnrollees()) {
			mtvPersionIds.add(enrollee.getMtvPersonID());
			for (Claim claim : enrollee.getClaims()) {
				stateCodes.add(claim.getStateCode());
				claimIds.add(claim.getClaimId());
			}
		}
		// setting contractId for validation if already not exist
		// or save before one week
		entity.setContractId(contract.getContractID());
		entity.setContractJson(convertIntoString(contract));
		entity.setNumberOfEnrollee(contract.getEnrollees().size());
		entity.setMtvPersonIds(String.join(",", mtvPersionIds));
		entity.setStateCodes(String.join(",", stateCodes));
		return entity;
	}

	private String convertIntoString(final Contract contract) {
		String contractStr = null;
		try {
			contractStr = objectMapper.writeValueAsString(contract);
		} catch (final JsonProcessingException e) {
			log.error("Unable to convert to string ", e);
			throw new RuntimeException("Error while create contract Json [" + e.getMessage() + "]");
		}
		return contractStr;
	}

	public Contract map(final String contractString) {
		Contract contract = null;
		try {
			contract = objectMapper.readValue(contractString.getBytes(), Contract.class);
		} catch (final IOException e) {
			log.error("Unable to convert to string ", e);
			throw new RuntimeException("Error while create contract Json [" + e.getMessage() + "]");
		}
		return contract;
	}

	public List<MemberContractClaimRequest> mapRequest(ContractEntity contractEntity) {

		List<MemberContractClaimRequest> requestList = new LinkedList<>();

		Contract contract = map(contractEntity.getContractJson());

		for (Enrollee enrollee : contract.getEnrollees()) {
			for (Claim claim : enrollee.getClaims()) {
				MemberContractClaimRequest request = map(contract.getContractID(), enrollee, claim);
				requestList.add(request);
			}
		}
		return requestList;
	}

	private MemberContractClaimRequest map(String contractId, Enrollee enrollee, Claim claim) {
		MemberContractClaimRequest validateProviderRequest = MemberContractClaimRequest.builder()
				.claimId(claim.getClaimId()) //
				.contractId(contractId) //
				.memberId(enrollee.getMemberId()) //
				.providerId(claim.getBillingProviderId()) //
				.operatorId("PCP-ING") //
				.state(claim.getStateCode()) //
				.build();
		return validateProviderRequest;
	}

}
