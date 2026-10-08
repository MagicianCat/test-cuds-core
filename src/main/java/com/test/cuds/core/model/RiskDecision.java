package com.test.cuds.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RiskDecision {
    private String riskDecisionCode;
    private String riskDecisionReason;
    private String firedRule;
    private List<Map<String, Object>> riskDecisionDetail;
}
