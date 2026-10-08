package com.test.cuds.core.context;

import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CudsSpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        CudsSpringApplicationContext.applicationContext = applicationContext;
        CudsSpringApplicationContext.environment = applicationContext.getBean(Environment.class);
    }

    public static <T> T getBean(String name) {
        return Objects.isNull(applicationContext) ? null : (T) applicationContext.getBean(name);
    }

    public static <T> T getBeanWithoutNull(String name) {
        T bean = getBean(name);
        if (Objects.isNull(bean)) {
            throw new FatalBeanException(name + "bean is null!");
        }

        return bean;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return Objects.isNull(applicationContext) ? null : applicationContext.getBean(requiredType);
    }

    public static <T> T getBeanWithoutNull(Class<T> requiredType) {
        T bean = getBean(requiredType);
        if (Objects.isNull(bean)) {
            throw new FatalBeanException(requiredType.getSimpleName() + "bean is null!");
        }

        return bean;
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return Objects.isNull(applicationContext) ? null : applicationContext.getBean(name, requiredType);
    }

    public static String getProperty(String key) {
        return Objects.isNull(environment) ? null : environment.getProperty(key);
    }
}
