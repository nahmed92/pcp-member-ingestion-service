package com.delta.pcpingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.IngestionStatsEntity;

@Repository
@Transactional
public interface IngestionStatsRepository  extends JpaRepository<IngestionStatsEntity, String> {
}
