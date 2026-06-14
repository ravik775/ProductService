package org.bgm.productservice.security.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.bgm.productservice.model.BaseModel;

@Entity
@Table(name="app_prod_role")
@Getter
@Setter
public class Roles extends BaseModel {
    @Column(unique = true, nullable = false)
    private String role;
    private String description;
}
