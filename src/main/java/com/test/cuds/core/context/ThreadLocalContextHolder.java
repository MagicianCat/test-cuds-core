package com.test.cuds.core.context;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalContextHolder {
    public static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<>();

    public static void setContext(String key, Object value) {
        if (!hasContext()) {
            contextHolder.set(Maps.newHashMap());
        }

        contextHolder.get().put(key, value);
    }

    public static Map<String, Object> getContext() {
        return contextHolder.get();
    }

    public static boolean hasContext() {
        return contextHolder.get() != null;
    }

    public static void initContext() {
        if (contextHolder.get() == null) {
            contextHolder.set(Maps.newHashMap());
        }
    }

    public static void clear() {
        contextHolder.remove();
    }
}
