package com.test.cuds.core.handler.impl;

import com.test.common.utils.ExceptionUtils;
import com.test.cuds.core.model.RiskDecision;
import com.test.cuds.core.handler.base.BaseRuleEngineHandler;
import com.test.cuvp.base.variable.base.BaseVariable;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Slf4j
@NoArgsConstructor
public class SimpleRuleEngineHandler extends BaseRuleEngineHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRuleEngineHandler.class);

    @Setter
    private String executorClassName;

    private Method executorExecuteRules;

    public void build() {
        try {
            Class<?> transformerClass = ClassUtils.getClass(executorClassName);
            executorExecuteRules = ReflectionUtils.findMethod(transformerClass, "executorExecuteRules", BaseVariable.class);
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][RULE_ERROR] SimpleRuleEngineHandler init fail", e);

            throw ExceptionUtils.buildBizException(e);
        }
    }


    @Override
    protected RiskDecision execute(BaseVariable baseVariable) {
        return (RiskDecision) ReflectionUtils.invokeMethod(executorExecuteRules, null, baseVariable);
    }
}
