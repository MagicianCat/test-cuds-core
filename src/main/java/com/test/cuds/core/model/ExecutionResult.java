package com.test.cuds.core.model;

import com.test.cuds.core.enums.ExecutionStatusEnum;
import com.test.cuvp.base.variable.base.BaseVariable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionResult {
    private BaseVariable baseVariable;
    private RiskDecision riskDecision;
    private ExecutionStatusEnum executionStatusEnum;
    private String errorCode;
}
