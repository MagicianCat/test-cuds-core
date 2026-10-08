package com.test.cuds.core.handler.base;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.test.common.enums.DeleteFlagEnum;
import com.test.common.error.ProcCodeEnum;
import com.test.common.utils.ClassUtils;
import com.test.common.utils.ExceptionUtils;
import com.test.common.utils.JsonUtils;
import com.test.cuds.core.constant.RiskConstants;
import com.test.cuds.core.entity.BaseRequestEntity;
import com.test.cuds.core.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public abstract class BaseRequestHandlerImpl<T extends BaseRequestEntity> implements RequestHandler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRequestHandlerImpl.class);

    @Override
    public T saveCheckRequest(Map<String, Object> requestParams, String requestSource) {
        try {
            Class<?> patternClass = ClassUtils.getPaternClass(this.getClass());
            BaseRequestEntity baseRequestEntity = (BaseRequestEntity) patternClass.getDeclaredConstructor().newInstance();
            baseRequestEntity.setAccountId((String) requestParams.get(RiskConstants.REQUEST_ENTITY_ACCOUNT_ID));
            baseRequestEntity.setRequestData(buildRequestData(requestParams));
            baseRequestEntity.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getDbValue());
            // TODO: need the real trace id tool
            String correlationId = UUID.randomUUID().toString().replace("-", "");
            baseRequestEntity.setCorrelationId(correlationId);

            processData((T) baseRequestEntity, requestParams, requestSource);

            if (RiskConstants.RISK_DEFAULT_SOURCE.equals(requestSource) || RiskConstants.RISK_DRY_RUN_SOURCE.equals(requestSource)) {
                this.getJpaRepository().saveAndFlush((T) baseRequestEntity);
                LOGGER.info("[Cuds-Core] save request success, id = {}, source = {}", baseRequestEntity.getId(), requestSource);
            } else {
                LOGGER.info("[Cuds-Core] Not prod or dry run source, skip saving request");
            }

            return (T) baseRequestEntity;
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][DATA_ERROR] save request exception", e);
            throw ExceptionUtils.buildBizException(ProcCodeEnum.PROC_CODE_000F1003);
        }
    }

    private String buildRequestData(Map<String, Object> requestParams) {
        Map<String, Object> params = Maps.newHashMap(requestParams);
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            if (this.getEncryptNameSet().contains(entry.getKey()) && Objects.nonNull(entry.getValue())) {
                params.put(entry.getKey(), Base64.getEncoder().encodeToString(((String) entry.getValue()).getBytes(StandardCharsets.UTF_8)));
            }
        }

        return JsonUtils.format(params);
    }

    protected Set<String> getEncryptNameSet() {
        return Sets.newHashSet();
    }

    protected void processData(T entity, Map<String, Object> requestParams, String requestSource) {

    }

    protected abstract JpaRepository<T, Long> getJpaRepository();
}
