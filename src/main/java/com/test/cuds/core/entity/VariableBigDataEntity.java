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
@Entity(name = "risk_variable_big_data")
public class VariableBigDataEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    protected Long id;

    @DataLength(value = 100)
    @Column(name = "NAME", length = 100)
    protected String name;

    @DataLength(value = 100)
    @Column(name = "DATA_HASH", length = 100)
    protected String dataHash;

    @Column(name = "VARIABLE_BIG_DATA", columnDefinition = "TEXT")
    protected String data;

    @Column(name = "IS_DELETED", nullable = false)
    protected Byte isDeleted;
}
