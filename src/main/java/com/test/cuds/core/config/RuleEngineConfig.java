package com.test.cuds.core.config;

import com.test.cuds.core.handler.impl.SimpleRuleEngineHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleEngineConfig {
    @Value("$settings.simpleRuleEngine.executorClassName")
    private String executorClassName;

    @Bean(name = "simpleRuleEngineHandler")
    @ConditionalOnExpression("settings.simpleRuleEngine.enable:false")
    public SimpleRuleEngineHandler simpleRuleEngineHandler(){
        SimpleRuleEngineHandler simpleRuleEngineHandler = new SimpleRuleEngineHandler();
        simpleRuleEngineHandler.setExecutorClassName(executorClassName);
        simpleRuleEngineHandler.build();

        return simpleRuleEngineHandler;
    }
}
