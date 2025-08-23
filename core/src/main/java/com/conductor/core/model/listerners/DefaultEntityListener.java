package com.conductor.core.model.listerners;

import com.conductor.core.model.BaseEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DefaultEntityListener {

    @PostPersist
    public void onPersist(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreatedOn(now());
        }
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdatedOn(now());
        }
    }



    private Timestamp now() {
        return Timestamp.from(
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        );
    }
}