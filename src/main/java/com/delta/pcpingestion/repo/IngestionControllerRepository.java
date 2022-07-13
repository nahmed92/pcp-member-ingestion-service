package com.delta.pcpingestion.repo;

import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.delta.pcpingestion.entity.IngestionControllerEntity;
import com.delta.pcpingestion.enums.ControlStatus;
import com.google.common.base.Optional;

@Repository
public interface IngestionControllerRepository extends JpaRepository<IngestionControllerEntity, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
	//@Query(value = "select top(1) * from dbo.ingestion_controller where status = 'CREATED' ", nativeQuery = true)
	Optional<IngestionControllerEntity> findFirstByStatusAndServiceInstanceId(ControlStatus status , String serviceInstanceId);
	
	List<IngestionControllerEntity> findAllByStatusAndServiceInstanceId(ControlStatus status , String serviceInstanceId);

}
