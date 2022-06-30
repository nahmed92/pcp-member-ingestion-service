package com.delta.pcpingestion.interservice.tibco.dto;

import java.util.List;

import lombok.Data;

/**
 * Enrollee Entity Class
 * 
 * @author ca94197
 * @since 1.0
 */

@Data
public class Enrollee {

	private String memberId;

	private String networkId;

	private String providerID;

	private String product;

	private String mtvPersonId;

	private MemberAddress memberAddress;

	private List<Claim> claims;

}
