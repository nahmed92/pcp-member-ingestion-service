package com.delta.pcpingestion.mapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.PCPMemberIngestionErrors;
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
		List<ContractEntity> memberContract = contracts.stream().map(this::map).collect(Collectors.toList());
		log.info("END Mapper.map()");
		return memberContract;
	}

	public ContractEntity merge(ContractEntity dbContractEntity, ContractEntity contractEntity) {
		log.info("START Mapper.merge");
		Contract dbContract = convertToContract(dbContractEntity.getContractJson());

		Contract contract = convertToContract(contractEntity.getContractJson());

		Contract mergedContract = merge(dbContract, contract);

		ContractEntity mergedContractEntity =  map(mergedContract);
        mergedContractEntity.setId(dbContractEntity.getId());
		log.info("END Mapper.merge");
		return mergedContractEntity;
	}

	private Contract merge(Contract dbContract, Contract contract) {

		if (StringUtils.equals(dbContract.getContractId(), contract.getContractId()) &&
				CollectionUtils.isNotEmpty(contract.getEnrollees())) {
				for (Enrollee enrollee : contract.getEnrollees()) {
					merge(dbContract, enrollee);
			}
		}
		return dbContract;
	}

	private void merge(Contract dbContract, Enrollee enrollee) {

		List<Enrollee> dbEnrollees = dbContract.getEnrollees();
		if (CollectionUtils.isEmpty(dbEnrollees)) {
			dbEnrollees = new ArrayList<>();
			dbEnrollees.add(enrollee);
			return;
		}
		for (Enrollee e : dbEnrollees) {
			if (StringUtils.equals(e.getMemberId(), enrollee.getMemberId())) {
				merge(e, enrollee.getClaims());

				return;
			}
		}
		dbEnrollees.add(enrollee);
	}

	private void merge(Enrollee e, List<Claim> claims) {
		if (CollectionUtils.isEmpty(e.getClaims())) {
			e.setClaims(claims);
			return;
		}
		if (CollectionUtils.isNotEmpty(claims)) {
			for (Claim claim : claims) {
				merge(e, claim);
			}
		}
	}

	private void merge(Enrollee e, Claim claim) {

		for (Claim c : e.getClaims()) {
			if (StringUtils.equals(c.getClaimId(), claim.getClaimId())) {
				return;
			}
		}
		e.getClaims().add(claim);
	}

	public ContractEntity map(Contract contract) {
		ContractEntity entity = ContractEntity.builder() //
				.publishStatus(PublishStatus.STAGED) //
				.numOfRetries(0) //
				.build();

		Set<String> stateCodes = new HashSet<>();
		Set<String> mtvPersionIds = new HashSet<>();
		Set<String> claimIds = new HashSet<>();

		for (Enrollee enrollee : contract.getEnrollees()) {
			mtvPersionIds.add(enrollee.getMtvPersonId());
			for (Claim claim : enrollee.getClaims()) {
				stateCodes.add(claim.getStateCode());
				claimIds.add(claim.getClaimId());
			}
		}
		// setting contractId for validation if already not exist
		// or save before one week
		entity.setContractId(contract.getContractId());
		entity.setContractJson(convertIntoString(contract));
		entity.setNumberOfEnrollee(contract.getEnrollees().size());
		entity.setMtvPersonIds(String.join(",", mtvPersionIds));
		entity.setClaimIds(String.join(",", claimIds));
		entity.setStateCodes(String.join(",", stateCodes));
		entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		return entity;
	}

	private String convertIntoString(final Contract contract) {
		String contractStr = null;
		try {
			contractStr = objectMapper.writeValueAsString(contract);
		} catch (final JsonProcessingException e) {
			log.error("Unable to convert to string ", e);
			throw PCPMemberIngestionErrors.PCP_SERVICE_ERROR.createException(e.getMessage());
		}
		return contractStr;
	}

	public Contract convertToContract(final String contractString) {
		Contract contract = null;
		try {
			contract = objectMapper.readValue(contractString.getBytes(), Contract.class);
		} catch (final IOException e) {
			log.error("Unable to convert to string ", e);
			throw PCPMemberIngestionErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		return contract;
	}

	public List<MemberContractClaimRequest> mapRequest(ContractEntity contractEntity) {

		List<MemberContractClaimRequest> requestList = new LinkedList<>();

		Contract contract = convertToContract(contractEntity.getContractJson());

		for (Enrollee enrollee : contract.getEnrollees()) {
			for (Claim claim : enrollee.getClaims()) {
				MemberContractClaimRequest request = map(contract.getContractId(), enrollee, claim);
				if(null != request) {
					requestList.add(request);	
				}				
			}
		}
		return requestList;
	}

	private MemberContractClaimRequest map(String contractId, Enrollee enrollee, Claim claim) {
		if(StringUtils.startsWithIgnoreCase(claim.getBillingProviderId(), "DC")) {
			return MemberContractClaimRequest.builder()
					.claimId(claim.getClaimId()) //
					.contractId(contractId) //
					.memberId(enrollee.getMemberId()) //
					.providerId(claim.getBillingProviderId()) //
					.operatorId("PCP-ING") //
					.state(claim.getStateCode()) //
					.build();
		}
		log.warn("Provider Id is not valid provider in claim {} ", claim);
		return null;
	}

}
