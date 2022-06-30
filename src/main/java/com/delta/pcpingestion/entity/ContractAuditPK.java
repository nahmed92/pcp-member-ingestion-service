package com.delta.pcpingestion.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ContractAuditPK implements Serializable {

    @Column(name = "id")
    private String id;

    @Column(name="revision_number")
    private Integer revisionNumber;
}
