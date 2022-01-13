package com.delta.pcpingestion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.delta.pcpingestion.client.ValidateProviderResponse;
import com.delta.pcpingestion.dto.PcpMember;
import com.delta.pcpingestion.entity.PCPMemberContractEntity;
import com.delta.pcpingestion.mtv.entities.MbrProvNtwkAssn;
import com.delta.pcpingestion.service.PCPIngestionService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.deltadental.platform.common.exception.ServiceError;
import com.deltadental.platform.common.exception.ServiceException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * PCP Ingestion Controller
 * 
 * @author ca94197
 * @since 1.0
 * 
 */
@RestController
@RequestMapping(value = "/pcpmembers/contract", produces = "application/json")
@Api(value = "/pcpmembers/contract")
@Slf4j
public class PcpIngestionController {

	@Autowired
	private PCPIngestionService service;

	@ApiOperation(value = "Create PCP Member contract")
	@ApiResponses({ @ApiResponse(code = 201, message = "Successfully Create PCP Member Contract"),
			@ApiResponse(code = 400, message = "Bad Request", response = ServiceError.class),
			@ApiResponse(code = 500, message = "Internal Server error", response = ServiceError.class) 
	})
	@PostMapping("/create")
	public ResponseEntity<Object> create(@RequestBody final PCPMembersRequest pcpMembersRequest) 
			throws ServiceException{
		log.info("PCPMembersRequest received to create {}", pcpMembersRequest);
		service.createPCPContract(pcpMembersRequest.getTibcoQueryStr());
		log.info("PCP member contract is created...");
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	/**
	 * This method will create pcp member
	 * 
	 * @param pcpmembers {@link PcpMember}
	 */
	@ApiOperation(value = "Get All PCP Member contract", response = PCPMemberContractEntity.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully Get PCP Member Contract",
	              response = PCPMemberContractEntity[].class),
			@ApiResponse(code = 400, message = "Bad Request", response = ServiceError.class),
			@ApiResponse(code = 500, message = "Internal Server error", response = ServiceError.class) })
	@GetMapping("/findAll")
	public ResponseEntity<List<PCPMemberContractEntity>> findAllContract()  throws ServiceException{
		log.info("Find All PCP Contract");
		List<PCPMemberContractEntity> contracts = service.getAllContract();
		return new ResponseEntity<List<PCPMemberContractEntity>>(contracts,HttpStatus.OK);
	}
	
    
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully Enable Disabled Tibco Service Call"),
        @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
        @ApiResponse(code = 404, message = "Unable validate provider.", response = ServiceError.class),
        @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
		@GetMapping(value = "/enableDisbaleTibcoServiceCall", produces = {MediaType.APPLICATION_JSON_VALUE})
		public void enableDisableTibcoService(@RequestParam("isUsedTibco") Boolean isUsedTibco) {
		service.enableDisbaleTibcoServiceCall(isUsedTibco);
     }
    
}
