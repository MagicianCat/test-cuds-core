package com.test.cuds.core.handler;

import com.test.cuds.core.entity.BaseDecisionEntity;
import com.test.cuds.core.entity.BaseRequestEntity;
import com.test.cuds.core.entity.VariableEntity;
import com.test.cuds.core.model.ExecutionResult;

public interface ResultHandler<T extends BaseDecisionEntity> {
    T getRiskDecisionSuccessResult(String idempotentId, String source);

    T saveRiskDecisionResult(BaseRequestEntity requestEntity, VariableEntity variableEntity, ExecutionResult executionResult);
}
