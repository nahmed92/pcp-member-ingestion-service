package com.delta.pcpingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delta.pcpingestion.entity.PCPIngestionActivity;
import com.delta.pcpingestion.enums.State;

@Repository
@Transactional
public interface PCPIngestionActivityRepository extends JpaRepository<PCPIngestionActivity, Integer>{

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "update pcpingestionactivity set num_claim_send_to_calculation = :numOfClaims, last_updated_date = CURRENT_TIMESTAMP "
			+ "Where state = :state and num_claim_send_to_calculation=0 ", nativeQuery = true)
	void updatePcpIngestionActivityForNumberOfClaim(int numOfClaims, State state);

}
