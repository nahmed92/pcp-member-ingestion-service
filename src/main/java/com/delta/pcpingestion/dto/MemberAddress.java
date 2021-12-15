package com.delta.pcpingestion.dto;

import lombok.Data;

/**
 * Member Address Class
 * 
 * @author ca94197
 * @since 1.0
 */

@Data
public class MemberAddress {

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private String zipCode;

}
