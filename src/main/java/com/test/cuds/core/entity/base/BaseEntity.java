package com.test.cuds.core.entity.base;

import com.google.common.collect.Lists;
import com.test.cuds.core.annotations.DataLength;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@Slf4j
public abstract class BaseEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEntity.class);

    @Column(updatable = false, name = "TIME_CREATED")
    protected long timeCreated;

    @Column(name = "TIME_UPDATED")
    protected long timeUpdated;

    @PrePersist
    protected void onCreate() {
        this.timeCreated = this.timeUpdated = System.currentTimeMillis();
    }

    protected void onUpdate() {
        this.timeUpdated = System.currentTimeMillis();
    }

    protected void prePersistAndUpdate() {
        List<Field> fields = getAllFields();

        fields.forEach(this::handleField);
    }

    private void handleField(final Field field) {
        DataLength dataLength = field.getAnnotation(DataLength.class);
        if (Objects.isNull(dataLength)) {
            return;
        }

        try {
            Object value = FieldUtils.readField(field, this, true);
            if (Objects.isNull(value) || !(value instanceof String)) {
                return;
            }

            value = StringUtils.substring((String) value, 0, dataLength.value());

            FieldUtils.writeField(field, this, value, true);
        } catch (Exception e) {
            LOGGER.info("BaseEntity sub string field throw exception", e);
        }
    }

    private List<Field> getAllFields() {
        List<Field> fields = Lists.newArrayList();
        Class<?> clazz = this.getClass();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return fields;
    }
}
