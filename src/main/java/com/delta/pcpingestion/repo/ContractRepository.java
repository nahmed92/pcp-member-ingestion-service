package com.delta.pcpingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.pcpingestion.entity.PCPMemberContractEntity;

/**
 * PCP Member Contract Repository
 * This enable to perform database level operation
 * 
 * @author ca94197
 * @since 1.0
 */
public interface ContractRepository extends JpaRepository<PCPMemberContractEntity, String> {

}
