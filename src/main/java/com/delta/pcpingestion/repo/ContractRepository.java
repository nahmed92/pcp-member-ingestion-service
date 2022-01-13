package com.delta.pcpingestion.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.pcpingestion.entity.PCPMemberContractEntity;
import com.delta.pcpingestion.service.STATUS;

/**
 * PCP Member Contract Repository This enable to perform database level
 * operation
 * 
 * @author ca94197
 * @since 1.0
 */
public interface ContractRepository extends JpaRepository<PCPMemberContractEntity, String> {

    List<PCPMemberContractEntity> findByStatus(STATUS status);

}
