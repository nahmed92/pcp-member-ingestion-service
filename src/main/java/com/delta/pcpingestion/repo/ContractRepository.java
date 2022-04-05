package com.delta.pcpingestion.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.Claim;
import com.delta.pcpingestion.entity.PCPMemberContract;

/**
 * PCP Member Contract Repository This enable to perform database level
 * operation
 * 
 * @author ca94197
 * @since 1.0
 */
@Repository
@Transactional
public interface ContractRepository extends JpaRepository<PCPMemberContract, String> {

	PCPMemberContract findByContractID(String contractId);

	@Query(value = "select * from dbo.contract contract"
	        + " inner join dbo.contract_claim claim"
			+ " on claim.pcpmembercontract_contract_id = contract.contract_id"
			+ " where contract.status = :status and claim.statecode = :state", nativeQuery = true)
	List<PCPMemberContract> findByStatusAndStateCode(int status, String state);
	
	@Query(value = "select * from dbo.contract_claim where PCPMemberContract_contract_id in :contractIds", nativeQuery = true)
	List<Claim> findAllClaimByContractIds(List<String> contractIds);
}
