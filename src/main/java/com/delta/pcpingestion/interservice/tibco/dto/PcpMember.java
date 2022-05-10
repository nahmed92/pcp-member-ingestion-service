package com.delta.pcpingestion.interservice.tibco.dto;

import java.util.List;

import lombok.Data;

/**
 * PCP Member Entity Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
public class PcpMember {

	private List<Contract> contracts;

}
