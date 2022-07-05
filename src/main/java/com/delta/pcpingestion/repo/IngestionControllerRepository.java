package com.delta.pcpingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.IngestionControllerEntity;

@Repository
@Transactional
public interface IngestionControllerRepository extends JpaRepository<IngestionControllerEntity, String> {

	@Query(value = "select top(1) * from dbo.ingestion_controller where status = 'CREATED' ", nativeQuery = true)
	IngestionControllerEntity read();

}
