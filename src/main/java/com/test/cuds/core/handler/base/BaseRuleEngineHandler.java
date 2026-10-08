package com.test.cuds.core.handler.base;

import com.test.cuds.core.model.RiskDecision;
import com.test.cuds.core.handler.RuleEngineHandler;
import com.test.cuds.core.model.ExecutionResult;
import com.test.cuds.core.enums.ExecutionStatusEnum;
import com.test.cuvp.base.variable.base.BaseVariable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public abstract class BaseRuleEngineHandler implements RuleEngineHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRuleEngineHandler.class);

    @Override
    public ExecutionResult executeRules(BaseVariable baseVariable) {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setBaseVariable(baseVariable);

        try {
            RiskDecision riskDecision = execute(baseVariable);

            // TODO: Metrics and tags
//            String decisionCode;
//            if (Objects.isNull(riskDecision) || StringUtils.isBlank(riskDecision.getRiskDecisionCode())) {
//                decisionCode = RiskConstants.UNKNOWN_DECISION_CODE;
//            } else {
//                decisionCode = riskDecision.getRiskDecisionCode();
//            }

            executionResult.setExecutionStatusEnum(ExecutionStatusEnum.SUCCESS);
            executionResult.setRiskDecision(riskDecision);
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][RULE_ERROR] RiskRuleEngineHandler execute rule error", e);

            executionResult.setExecutionStatusEnum(ExecutionStatusEnum.FAIL);
        }

        return executionResult;
    }

    protected abstract RiskDecision execute(BaseVariable baseVariable);
}
