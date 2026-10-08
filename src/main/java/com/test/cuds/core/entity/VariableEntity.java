package com.test.cuds.core.entity;

import com.test.cuds.core.annotations.DataLength;
import com.test.cuds.core.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "risk_variable")
public class VariableEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    protected Long id;

    @Column(name = "VARIABLE_BASIC_DATA", columnDefinition = "TEXT", nullable = false)
    protected String variableBasicData;

    @Column(name = "VARIABLE_BIG_DATA", columnDefinition = "TEXT")
    protected String variableBigData;

    @Column(name = "VARIABLE_ENCRYPT_DATA", columnDefinition = "TEXT")
    protected String variableEncryptData;

    @DataLength(value = 50)
    @Column(name = "REQUEST_SOURCE", length = 50, nullable = false)
    protected String requestSource;

    @Column(name = "IS_DELETED", nullable = false)
    protected Byte isDeleted;
}
