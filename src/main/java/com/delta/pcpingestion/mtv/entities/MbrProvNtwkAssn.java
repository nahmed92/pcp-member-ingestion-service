package com.delta.pcpingestion.mtv.entities;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MbrProvNtwkAssn {

	@Id
	@NonNull
	@Column(name = "contract_id")
	private String contractID;

	@Column(name = "member_Id")
	private String memberId;

	@Column(name = "effective_date")
	private Date effectiveDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "last_maint_ts")
	private Timestamp lastMaintTs;
}
