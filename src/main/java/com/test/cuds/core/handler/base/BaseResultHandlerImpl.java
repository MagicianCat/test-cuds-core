package com.test.cuds.core.handler.base;

import com.test.common.enums.DeleteFlagEnum;
import com.test.common.error.ProcCodeEnum;
import com.test.common.utils.ClassUtils;
import com.test.common.utils.ExceptionUtils;
import com.test.common.utils.JsonUtils;
import com.test.cuds.core.constant.RiskConstants;
import com.test.cuds.core.entity.BaseDecisionEntity;
import com.test.cuds.core.entity.BaseRequestEntity;
import com.test.cuds.core.entity.VariableEntity;
import com.test.cuds.core.enums.ExecutionStatusEnum;
import com.test.cuds.core.handler.ResultHandler;
import com.test.cuds.core.model.ExecutionResult;
import com.test.cuds.core.model.RiskDecision;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Slf4j
public abstract class BaseResultHandlerImpl<T extends BaseDecisionEntity> implements ResultHandler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResultHandlerImpl.class);

    @Override
    public T saveRiskDecisionResult(BaseRequestEntity requestEntity, VariableEntity variableEntity, ExecutionResult executionResult) {
        try {
            T decisionEntity = buildRiskDecisionEntity(requestEntity, variableEntity, executionResult);

            String requestSource = requestEntity.getRequestSource();
            if (RiskConstants.RISK_DEFAULT_SOURCE.equals(requestSource) || RiskConstants.RISK_DRY_RUN_SOURCE.equals(requestSource)) {
                this.getJpaRepository().saveAndFlush(decisionEntity);
                LOGGER.info("[Cuds-Core] save decision success, id = {}, source = {}", decisionEntity.getId(), requestSource);
            } else {
                LOGGER.info("[Cuds-Core] Not prod or dry run source, skip saving decision");
            }

            // TODO : In the future, we can save rule execution details here

            return decisionEntity;
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][DATA_ERROR] save decision exception", e);
            throw ExceptionUtils.buildBizException(ProcCodeEnum.PROC_CODE_000F1003);
        }
    }

    private T buildRiskDecisionEntity(BaseRequestEntity requestEntity, VariableEntity variableEntity, ExecutionResult executionResult) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> patternClass = ClassUtils.getPaternClass(this.getClass());
        BaseDecisionEntity decisionEntity = (BaseDecisionEntity) patternClass.getDeclaredConstructor().newInstance();

        ExecutionStatusEnum executionStatusEnum = executionResult.getExecutionStatusEnum();
        decisionEntity.setRequestId(requestEntity.getId());
        decisionEntity.setVariableId(variableEntity.getId());
        decisionEntity.setCorrelationId(requestEntity.getCorrelationId());
        decisionEntity.setExecutionStatus(executionStatusEnum.getDbValue());
        decisionEntity.setRequestSource(requestEntity.getRequestSource());
        decisionEntity.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getDbValue());

        if (ExecutionStatusEnum.SUCCESS.equals(executionStatusEnum)) {
            RiskDecision riskDecision = executionResult.getRiskDecision();
            if (Objects.isNull(riskDecision) || StringUtils.isBlank(riskDecision.getRiskDecisionCode())) {
                throw ExceptionUtils.buildBizException(ProcCodeEnum.PROC_CODE_000F1002, "risk decision code is required");
            }

            decisionEntity.setRiskFlowResult(riskDecision.getRiskDecisionCode());
            decisionEntity.setRiskFlowReason(riskDecision.getRiskDecisionReason());
            decisionEntity.setFiredRule(riskDecision.getFiredRule());
            String decisionDetail = CollectionUtils.isEmpty(riskDecision.getRiskDecisionDetail()) ? null : JsonUtils.format(riskDecision.getRiskDecisionDetail());
            decisionEntity.setRiskDecisionDetail(decisionDetail);
        }

        processData(requestEntity, decisionEntity, executionResult);

        return (T) decisionEntity;
    }

    protected void processData(BaseRequestEntity requestEntity, BaseDecisionEntity decisionEntity, ExecutionResult executionResult) {

    }

    protected abstract JpaRepository<T, Long> getJpaRepository();
}
