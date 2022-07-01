package com.delta.pcpingestion.repo;

import com.delta.pcpingestion.entity.ContractAuditEntity;
import com.delta.pcpingestion.entity.ContractAuditPK;
import com.delta.pcpingestion.entity.ContractEntity;
import com.delta.pcpingestion.enums.RevisionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 This class is responsible to add, update and delete the records in main table,
 along with adding the audit data into audit table.
 **/
@Component
public class ContractDAO {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ContractAuditRepository contractAuditRepository;


    public Optional<ContractEntity> findByContractId(String contractId){

        Optional<ContractEntity> contractEntity = contractRepository.findByContractId(contractId);
        return contractEntity;
    }

    public List<ContractEntity> findByPublishStatusAndStateCode(String publishStatus, String state){
        List<ContractEntity> contractEntities = contractRepository.findByPublishStatusAndStateCode(publishStatus, state);
        return contractEntities;
    }

    public void save(ContractEntity contractEntity){
    	//FIXME: read max row instead of find all
        Optional<List<ContractAuditEntity>> auditEntityOpt = contractAuditRepository.findAllByContractId(contractEntity.getContractId());
        int max = 0;
        if(auditEntityOpt.isPresent()) {
            max = auditEntityOpt.get().stream().mapToInt(c -> c.getContractAuditPK().getRevisionNumber()).max().getAsInt();
            max++;
        }
        ContractAuditEntity contractAuditEntity =  buildContractAuditEntity(contractEntity, max);
        contractRepository.save(contractEntity);

        contractAuditRepository.save(contractAuditEntity);
    }

    private ContractAuditEntity buildContractAuditEntity(ContractEntity contractEntity, int max) {

        ContractAuditEntity contractAuditEntity = ContractAuditEntity.builder().
            contractAuditPK(ContractAuditPK.builder().id(contractEntity.getId()).revisionNumber(max).build() )
                .contractId(contractEntity.getContractId())
                .claimIds(contractEntity.getClaimIds())
                .numOfRetries(contractEntity.getNumOfRetries())
                .contractJson(contractEntity.getContractJson())
                .publishStatus(contractEntity.getPublishStatus())
                .mtvPersonIds(contractEntity.getMtvPersonIds())
                .numberOfEnrollee(contractEntity.getNumberOfEnrollee())
                .stateCodes(contractEntity.getStateCodes())
                .createdAt(contractEntity.getCreatedAt())
                .lastUpdatedAt(contractEntity.getLastUpdatedAt())
                .revisionType(max == 0 ? RevisionType.CREATE : RevisionType.UPDATE)
                .build();
         return contractAuditEntity;
    }
}
