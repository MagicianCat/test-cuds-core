package com.test.cuds.core.enums;

import lombok.Getter;

public enum ExecutionStatusEnum {
    SUCCESS("Execute success", "SUCCESS"),
    FAIL("Execute fail", "FAIL");

    @Getter
    private final String detail;
    @Getter
    private final String dbValue;

    ExecutionStatusEnum(final String detail, final String dbValue) {
        this.detail = detail;
        this.dbValue = dbValue;
    }
}
