package com.test.cuds.core.aspect;

import com.test.common.utils.ExceptionUtils;
import com.test.cuds.core.context.ThreadLocalContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ThreadLocalContextAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalContextAspect.class);

    @Before("@annotation(threadLocalContext)")
    public void logContextBefore(JoinPoint jp, ThreadLocalContext threadLocalContext) {
        ThreadLocalContextHolder.initContext();

        try {
            // Validation
            if (!Objects.equals(threadLocalContext.name().length, threadLocalContext.value().length)) {
                throw ExceptionUtils.buildBizException("name length and value length not equal.");
            }

            // Set context from annotation param
            for (int i=0; i<threadLocalContext.name().length; i++) {
                ThreadLocalContextHolder.setContext(threadLocalContext.name()[i], threadLocalContext.value()[i]);
            }
        } catch (Exception e) {
            LOGGER.warn("get threadLocalContext exception", e);
        }
    }

    @AfterThrowing("@annotation(threadLocalContext)")
    public void logContextAfterThrowing(JoinPoint jp) {
        ThreadLocalContextHolder.clear();
    }

    @AfterReturning("@annotation(threadLocalContext)")
    public void logContextAfterReturning(JoinPoint jp) {
        ThreadLocalContextHolder.clear();
    }
}
