package com.test.cuds.core.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.cuvp.base.variable.base.BaseVariable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RiskVariableContext {
    private BaseVariable rootBaseVariable;
    private BaseVariable currBaseVariable;
    private List<Map<String, Object>> variableBigDataList = Lists.newArrayList();
    private List<Map<String, Object>> variableEncryptDataList = Lists.newArrayList();
    private Map<String, Map<String, Object>> variableBigDataMap = Maps.newHashMap();
    private Map<String, Map<String, Object>> variableEncryptDataMap = Maps.newHashMap();
}
