package com.test.cuds.core.handler;

import com.test.cuds.core.model.ExecutionResult;
import com.test.cuvp.base.variable.base.BaseVariable;

public interface RuleEngineHandler {
    ExecutionResult executeRules(BaseVariable baseVariable);
}
