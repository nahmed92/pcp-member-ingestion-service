package com.delta.pcpingestion.repo;

import com.delta.pcpingestion.entity.ContractAuditEntity;
import com.delta.pcpingestion.entity.ContractAuditPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
interface ContractAuditRepository extends JpaRepository<ContractAuditEntity, ContractAuditPK> {
    Optional<List<ContractAuditEntity>> findAllByContractId(String contractId);

}
