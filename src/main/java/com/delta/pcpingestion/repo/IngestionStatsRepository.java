package com.delta.pcpingestion.repo;

import com.delta.pcpingestion.entity.IngestionStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IngestionStatsRepository  extends JpaRepository<IngestionStatsEntity, String> {
}
