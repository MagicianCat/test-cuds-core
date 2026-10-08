package com.test.cuds.core.aspect;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadLocalContext {
    String[] name() default {};
    String[] value() default {};
}
