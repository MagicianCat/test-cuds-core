package com.test.cuds.core.context;

import com.test.cuds.core.handler.impl.VariableHandlerImpl;
import com.test.cuds.core.repository.VariableBigDataRepository;
import com.test.cuds.core.repository.VariableRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VariableConfig {
    @Bean(name = "variableHandler")
    @ConditionalOnExpression("${settings.variable.handler.enable:false}")
    public VariableHandlerImpl variableHandler(VariableBigDataRepository variableBigDataRepository, VariableRepository variableRepository) {
        VariableHandlerImpl variableHandler = new VariableHandlerImpl();
        variableHandler.setVariableRepository(variableRepository);
        variableHandler.setVariableBigDataRepository(variableBigDataRepository);

        return variableHandler;
    }
}
