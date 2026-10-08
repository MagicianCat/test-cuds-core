package com.test.cuds.core.handler;

import com.test.cuds.core.entity.BaseRequestEntity;

import java.util.Map;

public interface RequestHandler<T extends BaseRequestEntity> {
    T saveCheckRequest(Map<String, Object> requestParams, String requestSource);
}
