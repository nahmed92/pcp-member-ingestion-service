package com.delta.pcpingestion.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.delta.pcpingestion.client.PcpCalculationServiceClient;
import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.dto.Contract;
import com.delta.pcpingestion.dto.Enrollee;
import com.delta.pcpingestion.dto.Member;
import com.delta.pcpingestion.entity.PCPMemberContractEntity;
import com.delta.pcpingestion.repo.ContractRepository;
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
	

	public void createPCPContract(final String pcpMembersRequestStr) {
		log.info("createPCPContract - Request to create pcp member contract started");
		ResponseEntity<Member> members = fetchPcpmemberFromTibco(pcpMembersRequestStr);
		log.debug("Member Receive {}", members);
		if(members != null) {
		List<PCPMemberContractEntity> contract = buildPcpMemberContract(members.getBody().getPcpMembers().getContracts());
		if(contract.size() > 0) {
		repository.saveAll(contract);
		}else {
		log.info("There is no contract to save..");
		}
	  }
	}	
	
	public List<PCPMemberContractEntity> buildPcpMemberContract(final List<Contract> contracts) {
		log.debug("buildPcpContract - Start Bulding list of contract from Tibco response...");	
		 List<PCPMemberContractEntity> memberContract = contracts.stream()
		 .map(contract ->  new PCPMemberContractEntity(contract.getContractID(),
				 listOfEnrollMembers(contract.getEnrollees()),
		  contract.getGroupNumber(),contract.getDivisionNumber(),
		  convertIntoString(contract), contract.getEnrollees().size(), STATUS.STAGED, 0))
		 .collect(Collectors.toList());			
			log.debug("buildPcpContractcontract Ends contract are is {}", memberContract);         
			return memberContract;
		}
	
	public List<PCPMemberContractEntity> getAllContract() {
		return repository.findAll();
	}
	
	public void validateProvider() {
		  log.info("Validate Provider"+ pcpCalculationClient.validateProvider());		
	}	
	
	public ResponseEntity<Member> fetchPcpmemberFromTibco(String pcpMembersRequest) {
		return tibcoClient.fetchPcpmemberFromTibco(pcpMembersRequest);
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
	
	private List<String> listOfEnrollMembers(List<Enrollee> enrolles){		
		return enrolles.stream().map(enrollee -> enrollee.getMemberId()).distinct().collect(Collectors.toList());
	}
}
