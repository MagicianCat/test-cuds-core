package com.test.cuds.core.entity;

import com.test.cuds.core.annotations.DataLength;
import com.test.cuds.core.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseRequestEntity extends BaseEntity {
    @DataLength(value = 100)
    @Column(name = "ACCOUNT_ID", length = 100)
    protected String accountId;

    @DataLength(value = 100)
    @Column(name = "CORRELATION_ID", length = 100, nullable = false)
    protected String correlationId;

    @DataLength(value = 4000)
    @Column(name = "REQUEST_DATA", length = 4000)
    protected String requestData;

    @DataLength(value = 50)
    @Column(name = "REQUEST_SOURCE", length = 50, nullable = false)
    protected String requestSource;

    @Column(name = "IS_DELETED", nullable = false)
    protected Byte isDeleted;

    public abstract Long getId();
}
