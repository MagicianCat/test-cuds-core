package com.test.cuds.core.handler;

import com.test.cuds.core.entity.VariableEntity;
import com.test.cuvp.base.variable.base.BaseVariable;

public interface VariableHandler {
    VariableEntity saveVariable(BaseVariable baseVariable, String source);

    BaseVariable getVariable(Long variableId, String variableClassName);
}
