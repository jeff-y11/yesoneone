package com.geodevai.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.geodevai.config.CustomAuditingEntityListener;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(CustomAuditingEntityListener.class)
public abstract class AuditableEntity {

    @Embedded
    @JsonIgnore
    AuditFields auditFields = new AuditFields();

    private static final Map<Class<?>, Field> ID_FIELD_CACHE = new ConcurrentHashMap<>();

    private static Field resolveIdField(Class<?> clazz) {
        return ID_FIELD_CACHE.computeIfAbsent(clazz, cls -> {
            while (cls != null && cls != Object.class) {
                for (Field field : cls.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field;
                    }
                }
                cls = cls.getSuperclass();
            }
            throw new IllegalStateException("No @Id field found in class: " + clazz.getName());
        });
    }

    @JsonIgnore
    public Object getIdValue() {
        try {
            Field idField = resolveIdField(this.getClass());
            return idField.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access ID field in " + this.getClass(), e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || this.getClass() != other.getClass())
            return false;

        Object thisId = this.getIdValue();
        Object otherId = ((AuditableEntity) other).getIdValue();

        if (thisId == null || otherId == null)
            return false;

        return thisId.equals(otherId);
    }

    @Override
    public int hashCode() {
        Object idValue = getIdValue();
        return (idValue != null) ? idValue.hashCode() : System.identityHashCode(this);
    }
}