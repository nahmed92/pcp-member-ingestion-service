package com.delta.pcpingestion.entity;


import java.sql.Date;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name="Claim")
@Embeddable
public class Claim {// implements Comparable<Claim>{

	private String claimId;

	private String billingProviderId;

	private String billProviderSpeciality;

	@JsonFormat(pattern="dd-MMM-yy")
	private Date receivedDate;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date resolvedDate;

	private String serviceNumber;
	
	private String emergencyFlag;

	private String encounterFlag;
	
	private String claimStatus;
	
	private String stateCode;
	
	private String securityGroupId;
	
//	 @Override
//	  public int compareTo(Claim claim) {
//	    if (this.getReceivedDate() == null || claim.getReceivedDate() == null)
//	      return 0;
//	    return claim.getReceivedDate().compareTo(this.getReceivedDate());
//	  }
	
}
