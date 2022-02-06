package com.delta.pcpingestion.mtv.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.delta.pcpingestion.mtv.entities.MbrProvNtwkAssn;

public interface MetavanceRepo extends JpaRepository<MbrProvNtwkAssn, Long> {
	
  @Query(
	      value =
	          "select CONTRACT_ID, member_id, EFFECTIVE_DATE, end_date, LAST_MAINT_TS from MVPPPRD.MBR_PROV_NTWK_ASSN "
	          + "where  VOID_FLAG='N' and audit_flag='N' and trim(PROVIDER_ID) is null and LAST_MAINT_TS > SYSDATE - 10 and end_date > "
	          + "sysdate and CONTRACT_ID in (select CONTRACT_ID from MVPPPRD.MEMBER_ELIGIBILITY "
	          + "where bus_level_6_id=cast(:state as CHAR(25)) and bus_level_5_id='DDIC DHMO'  and VOID_FLAG='N' and audit_flag='N' and end_date > sysdate)"
	          + "and rownum < 100 order by LAST_MAINT_TS" , nativeQuery = true)
	List<MbrProvNtwkAssn> findAllFirstHunderedMTVMemberContractEntity(@Param("state") String state);

  @Query(
	      value =
	          "select CONTRACT_ID, member_id, EFFECTIVE_DATE, end_date, LAST_MAINT_TS from MVPPPRD.MBR_PROV_NTWK_ASSN "
	          + "where  VOID_FLAG='N' and audit_flag='N' and trim(PROVIDER_ID) is null and LAST_MAINT_TS > SYSDATE - 10 and end_date > sysdate "
	          + "and CONTRACT_ID in (select CONTRACT_ID from MVPPPRD.MEMBER_ELIGIBILITY "
	          + "where bus_level_6_id=cast(:state as CHAR(25)) and bus_level_5_id='DDIC DHMO'  and VOID_FLAG='N' and audit_flag='N' "
	          + "and end_date > sysdate) and LAST_MAINT_TS > cast(:last_maint_ts as CHAR(50)) and rownum < 100 order by LAST_MAINT_TS" ,
	          nativeQuery = true)
	List<MbrProvNtwkAssn> findAllAfterFirstHunderedMTVMemberContractEntity(@Param("state") String state, @Param("last_maint_ts") Timestamp last_maint_ts);

}
