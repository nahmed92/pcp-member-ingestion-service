package com.delta.pcpingestion.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delta.pcpingestion.entity.PCPMemberContract;
import com.delta.pcpingestion.service.STATUS;

/**
 * PCP Member Contract Repository This enable to perform database level
 * operation
 * 
 * @author ca94197
 * @since 1.0
 */
@Repository
public interface ContractRepository extends JpaRepository<PCPMemberContract, String> {

    List<PCPMemberContract> findByStatus(STATUS status);

}
