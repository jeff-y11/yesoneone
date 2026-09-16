package com.geodevai.data.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditFields {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private UUID createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedBy
    @Column(nullable = false)
    private UUID modifiedBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modified;

    @Column(name = "updated_source", length = 255)
    private String updatedSource;

    @Column(name = "created_by_name", length = 255, updatable = false)
    private String createdByName;

    @Column(name = "modified_by_name", length = 255)
    private String modifiedByName;
}
