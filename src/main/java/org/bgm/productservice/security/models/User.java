package org.bgm.productservice.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.bgm.productservice.model.BaseModel;

@Entity
@Table(name="app_product_user")
@Getter
@Setter
public class User extends BaseModel {
    private String username;
    private String password;
    private String[] roles;
    private boolean accountExpired = false;
    private boolean enabled = true;
    private boolean credentialsExpired = false;
    private boolean accountLocked = false;
}
