package com.delta.pcpingestion.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.ContractEntity;

@Repository
@Transactional
interface ContractRepository extends JpaRepository<ContractEntity, String> {

	Optional<ContractEntity> findByContractId(String contractId);

	@Query(value = "select * from dbo.contract where publish_status = :publishStatus and state_codes like %:state% ", nativeQuery = true)
	List<ContractEntity> findByPublishStatusAndStateCode(String publishStatus, String state);

}
