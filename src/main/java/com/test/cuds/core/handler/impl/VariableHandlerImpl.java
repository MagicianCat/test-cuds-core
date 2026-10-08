package com.test.cuds.core.handler.impl;

import com.google.common.collect.ImmutableMap;
import com.test.common.enums.DeleteFlagEnum;
import com.test.common.utils.ExceptionUtils;
import com.test.common.utils.JsonUtils;
import com.test.common.utils.RiskObjectUtils;
import com.test.cuds.core.constant.RiskConstants;
import com.test.cuds.core.entity.VariableBigDataEntity;
import com.test.cuds.core.entity.VariableEntity;
import com.test.cuds.core.handler.VariableHandler;
import com.test.cuds.core.model.RiskVariableContext;
import com.test.cuds.core.repository.VariableBigDataRepository;
import com.test.cuds.core.repository.VariableRepository;
import com.test.cuvp.base.variable.annotation.RiskBigDataVariable;
import com.test.cuvp.base.variable.annotation.RiskEncryptVariable;
import com.test.cuvp.base.variable.base.AbstractMultiFieldVariable;
import com.test.cuvp.base.variable.base.BaseVariable;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@NoArgsConstructor
public class VariableHandlerImpl implements VariableHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariableHandlerImpl.class);

    @Setter
    private VariableBigDataRepository variableBigDataRepository;
    @Setter
    private VariableRepository variableRepository;

    @Override
    public VariableEntity saveVariable(BaseVariable baseVariable, String source) {
        RiskObjectUtils.validateNotNull(baseVariable, "variable");

        RiskVariableContext riskVariableContext = new RiskVariableContext();
        riskVariableContext.setRootBaseVariable(baseVariable);

        iterateOverVariable(riskVariableContext, this::processSpecialVariableData);

        try {
            VariableEntity variableEntity = new VariableEntity();
            variableEntity.setVariableBasicData(JsonUtils.format(baseVariable));
            variableEntity.setVariableBigData(JsonUtils.format(riskVariableContext.getVariableBigDataList()));
            variableEntity.setVariableEncryptData(JsonUtils.format(riskVariableContext.getVariableEncryptDataList()));
            variableEntity.setRequestSource(source);
            variableEntity.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getDbValue());

            if (RiskConstants.RISK_DEFAULT_SOURCE.equals(source) || RiskConstants.RISK_DRY_RUN_SOURCE.equals(source)) {
                variableRepository.saveAndFlush(variableEntity);
                LOGGER.info("[Cuds-Core] save variable success, id = {}, source = {}", variableEntity.getId(), source);
            } else {
                LOGGER.info("[Cuds-Core] Not prod or dry run source, skip saving variable");
            }

            return variableEntity;
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][VARIABLE_ERROR] save variable exception", e);
            throw ExceptionUtils.buildBizException(e);
        }
    }

    private void processSpecialVariableData(RiskVariableContext riskVariableContext, Field field, String name) {
        try {
            Object value = FieldUtils.readField(field, riskVariableContext.getCurrBaseVariable(), true);
            if (Objects.isNull(value)) {
                return;
            }

            if (field.isAnnotationPresent(RiskBigDataVariable.class)) {
                Long bigDataId = saveVariableBigData(name, value);
                riskVariableContext.getVariableBigDataList().add(buildVariableBigDataPropertyMap(name, bigDataId));
            } else if (field.isAnnotationPresent(RiskEncryptVariable.class)) {
                riskVariableContext.getVariableEncryptDataList().add(buildVariableEcryptDataPropertyMap(name, value));
            }
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][VARIABLE_ERROR] process saving special variable exception", e);
            throw ExceptionUtils.buildBizException(e);
        }
    }

    private Long saveVariableBigData(String name, Object value) {
        String valueString = JsonUtils.format(value);
        String valueHash = DigestUtils.md5Hex(valueString);
        List<VariableBigDataEntity> variableBigDataEntityList = variableBigDataRepository.findByNameAndDataHashAndIsDeleted(name, valueHash, DeleteFlagEnum.NOT_DELETED.getDbValue());
        if (!CollectionUtils.isEmpty(variableBigDataEntityList)) {
            return variableBigDataEntityList.get(0).getId();
        }

        VariableBigDataEntity variableBigDataEntity = new VariableBigDataEntity();
        variableBigDataEntity.setName(name);
        variableBigDataEntity.setData(valueString);
        variableBigDataEntity.setDataHash(valueHash);
        variableBigDataEntity.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getDbValue());
        variableBigDataEntity = variableBigDataRepository.saveAndFlush(variableBigDataEntity);

        return variableBigDataEntity.getId();
    }

    private static Map<String, Object> buildVariableBigDataPropertyMap(String name, Long id) {
        return new ImmutableMap.Builder<String, Object>()
                .put("name", name)
                .put("id", id)
                .build();
    }

    private static Map<String, Object> buildVariableEcryptDataPropertyMap(String name, Object object) {
        return new ImmutableMap.Builder<String, Object>()
                .put("name", name)
                .put("data", Base64.getEncoder().encodeToString(JsonUtils.format(object).getBytes(StandardCharsets.UTF_8)))
                .build();
    }

    @Override
    public BaseVariable getVariable(Long variableId, String variableClassName) {
        List<VariableEntity> variableEntities = variableRepository.findByIdAndIsDeleted(variableId, DeleteFlagEnum.NOT_DELETED.getDbValue());
        if (CollectionUtils.isEmpty(variableEntities)) {
            return null;
        }
        VariableEntity variableEntity = variableEntities.get(0);

        try {
            Class<?> variableClass = ClassUtils.getClass(variableClassName);
            BaseVariable baseVariable = (BaseVariable) JsonUtils.parse(variableEntity.getVariableBasicData(), variableClass);

            RiskVariableContext riskVariableContext = parseSpecialVariableDataFromDB(variableEntity);
            riskVariableContext.setRootBaseVariable(baseVariable);

            iterateOverVariable(riskVariableContext, this::parseSpecialVariable);

            return baseVariable;
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][VARIABLE_ERROR] parse variable exception", e);
            throw ExceptionUtils.buildBizException(e);
        }
    }

    private static RiskVariableContext parseSpecialVariableDataFromDB(VariableEntity variableEntity) {
        List<Map<String, Object>> variableBigDataList = JsonUtils.parse(variableEntity.getVariableBigData(), List.class);
        List<Map<String, Object>> variableEncryptDataList = JsonUtils.parse(variableEntity.getVariableEncryptData(), List.class);

        RiskVariableContext riskVariableContext = new RiskVariableContext();
        Map<String, Map<String, Object>> variableBigDataMap = riskVariableContext.getVariableBigDataMap();
        for (Map<String, Object> bigData: variableBigDataList) {
            variableBigDataMap.put((String) bigData.get("name"), bigData);
        }

        Map<String, Map<String, Object>> variableEncryptDataMap = riskVariableContext.getVariableEncryptDataMap();
        for (Map<String, Object> encryptData : variableEncryptDataList) {
            variableEncryptDataMap.put((String) encryptData.get("name"), encryptData);
        }

        return riskVariableContext;
    }

    private void parseSpecialVariable(RiskVariableContext riskVariableContext, Field field, String name) {
        try {
            doParseSpecialVariable(riskVariableContext, field, name);
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][VARIABLE_ERROR] parse special variable exception", e);
            throw ExceptionUtils.buildBizException(e);
        }
    }

    private void doParseSpecialVariable(RiskVariableContext riskVariableContext, Field field, String name) throws IllegalAccessException {
        BaseVariable baseVariable = riskVariableContext.getCurrBaseVariable();

        if (field.isAnnotationPresent(RiskBigDataVariable.class)) {
            Map<String, Object> bigData = riskVariableContext.getVariableBigDataMap().get(name);
            if (Objects.isNull(bigData)) {
                return;
            }

            Long bigDataId = Long.parseLong((String) bigData.get("id"));
            String bigDataValue = getVariableBigDataByExistId(bigDataId);

            FieldUtils.writeField(field, baseVariable, JsonUtils.parse(bigDataValue, field.getType()), true);
        } else if (field.isAnnotationPresent(RiskEncryptVariable.class)) {
            Map<String, Object> encryptData = riskVariableContext.getVariableEncryptDataMap().get(name);
            if (Objects.isNull(encryptData)) {
                return;
            }

            byte[] encryptDataBytes = Base64.getDecoder().decode((String) encryptData.get("data"));
            String encryptDataValue = new String(encryptDataBytes);
            FieldUtils.writeField(field, baseVariable, JsonUtils.parse(encryptDataValue, field.getType()), true);
        }
    }

    private String getVariableBigDataByExistId(Long id) {
        List<VariableBigDataEntity> variableBigDataEntityList = variableBigDataRepository.findByIdAndIsDeleted(id, DeleteFlagEnum.NOT_DELETED.getDbValue());
        if (CollectionUtils.isEmpty(variableBigDataEntityList)) {
            return null;
        }

        return variableBigDataEntityList.get(0).getData();
    }

    private void iterateOverVariable(RiskVariableContext riskVariableContext,
                                     VariableProcessor<RiskVariableContext, Field> variableProcessor) {
        BaseVariable baseVariable = riskVariableContext.getRootBaseVariable();

        Field[] fields = baseVariable.getClass().getDeclaredFields();
        try {
            doIterateOverVariable(riskVariableContext, variableProcessor, baseVariable, fields);
        } catch (Exception e) {
            LOGGER.error("[Cuds-Core][FAIL][VARIABLE_ERROR] iterate variable exception", e);
            throw ExceptionUtils.buildBizException(e);
        }
    }

    private static void doIterateOverVariable(RiskVariableContext riskVariableContext,
                                              VariableProcessor<RiskVariableContext, Field> variableProcessor,
                                              BaseVariable baseVariable, Field[] fields) throws IllegalAccessException {
        for (Field field: fields) {
            if (AbstractMultiFieldVariable.class.isAssignableFrom(field.getType())) {
                BaseVariable multiFieldVariable = (BaseVariable) FieldUtils.readField(field, baseVariable, true);
                Field[] multiFieldVariableFields = field.getType().getDeclaredFields();
                if (Objects.isNull(multiFieldVariable) || ArrayUtils.isEmpty(multiFieldVariableFields)) {
                    continue;
                }

                riskVariableContext.setCurrBaseVariable(multiFieldVariable);
                for (Field multiFieldVariableField : multiFieldVariableFields) {
                    variableProcessor.process(riskVariableContext, multiFieldVariableField, getVariableName(multiFieldVariableField, field));
                }
            }
        }
    }

    private static String getVariableName(Field field, Field parentField) {
        return parentField.getType().getSimpleName() + "." +field.getName();
    }

    private interface VariableProcessor<T, R> {
        void process(T t, R r, String name);
    }
}
