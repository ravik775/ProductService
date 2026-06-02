package org.bgm.productservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;

    private boolean deleted;

    @Column(nullable = false, updatable = false)
    private long createdAt;

    @Column(nullable = false)
    private long modifiedAt;

    @PrePersist
    protected void onCreate(){
        sanitize();
        createdAt = System.currentTimeMillis();
        if(createdBy == null || createdBy.isBlank()) createdBy="UNKNOWN";
        updatedBy=createdBy;
        modifiedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate(){
        sanitize();
        modifiedAt = System.currentTimeMillis();
        if(updatedBy == null || updatedBy.isBlank()) updatedBy="UNKNOWN";


    }

    protected void sanitize(){
        // nothing just a hook
    }

    protected String normalize(String value) {
        return value
                .trim()
                .replaceAll("\\s+", " ");
    }
}
