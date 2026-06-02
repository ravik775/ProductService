package org.bgm.productservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name="ProductCategory")
public class Category extends BaseModel{
    @Column(unique = true, nullable = false, length = 40)
    private String name;

    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

    @Override
    protected void sanitize() {
        description = normalize(description);
        name = normalize(name);
    }
}
