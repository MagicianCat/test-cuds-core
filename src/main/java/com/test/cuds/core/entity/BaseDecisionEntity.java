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
public abstract class BaseDecisionEntity extends BaseEntity {
    @Column(name = "ACCOUNT_ID", nullable = false)
    protected Long requestId;

    @Column(name = "VARIABLE_ID", nullable = false)
    protected Long variableId;

    @DataLength(value = 100)
    @Column(name = "CORRELATION_ID", length = 100, nullable = false)
    protected String correlationId;

    @DataLength(value = 100)
    @Column(name = "RISK_FLOW_RESULT", length = 100, nullable = false)
    protected String riskFlowResult;

    @DataLength(value = 4000)
    @Column(name = "RISK_FLOW_REASON", length = 4000)
    protected String riskFlowReason;

    @DataLength(value = 100)
    @Column(name = "RISK_DECISION_DETAIL", length = 100)
    protected String riskDecisionDetail;

    @DataLength(value = 200)
    @Column(name = "FIRED_RULE", length = 200)
    protected String firedRule;

    @DataLength(value = 20)
    @Column(name = "EXECUTION_STATUS", length = 20)
    protected String executionStatus;

    @DataLength(value = 50)
    @Column(name = "REQUEST_SOURCE", length = 50, nullable = false)
    protected String requestSource;

    @Column(name = "IS_DELETED", nullable = false)
    protected Byte isDeleted;

    public abstract Long getId();
}
