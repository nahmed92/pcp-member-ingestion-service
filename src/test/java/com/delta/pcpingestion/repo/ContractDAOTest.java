package com.delta.pcpingestion.repo;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delta.pcpingestion.entity.ContractAuditEntity;
import com.delta.pcpingestion.entity.ContractAuditPK;
import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.State;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When Contract DAO Test")
@Configuration
@EntityScan(basePackages = {"com.delta.pcpingestion.*"})
@Slf4j
public class ContractDAOTest {
	
    @Mock
    private ContractRepository contractRepository;
    
    @Mock
    private ContractAuditRepository contractAuditRepository;
    
    @InjectMocks
    private ContractDAO contractDAO;
    
   @Test
    public void testSindByContractId() { 
    	doReturn(Optional.of(getContractEntity())).when(contractRepository).findByContractId("1910012");
    	Optional<ContractEntity> contractEntity = contractDAO.findByContractId("1910012");
    	Assertions.assertNotNull(contractEntity);
    }
   
   @Test
   public void testFindByPublishStatusAndStateCode() {	   
   	doReturn(List.of(getContractEntity())).when(contractRepository).findByPublishStatusAndStateCode("Active", State.CA.toString());
   	List<ContractEntity>  contractEntity = contractDAO.findByPublishStatusAndStateCode("Active", State.CA.toString());
   	Assertions.assertNotNull(contractEntity);
   }
   
   @Test
   public void testSave() {	 
	ContractAuditPK pk = ContractAuditPK.builder()
			.id("1241")
			.revisionNumber(3)
			.build();
	ContractAuditEntity auditEntity = ContractAuditEntity.builder()
			.contractAuditPK(pk)
			.claimIds("1221133")			
			.build();
   	doReturn(Optional.of(List.of(auditEntity))).when(contractAuditRepository).findAllByContractId("1910012");
   	contractDAO.save(getContractEntity());
   	verify(contractAuditRepository, atLeastOnce()).save(ArgumentMatchers.any());
   }
   
   private ContractEntity getContractEntity() {
	   	return ContractEntity.builder()
    			.contractId("1910012")
    			.claimIds("1221133,2113311")
    			.mtvPersonIds("111222,222111")
    			.id(UUID.randomUUID().toString())
    			.build();
   }

}
