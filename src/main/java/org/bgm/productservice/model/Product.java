package org.bgm.productservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="Product")
public class Product extends BaseModel{
    @Column(unique = true, nullable = false, length =40)
    private String title;
    private long price;
    private String description;
    private String image;

    @ManyToOne
    private Category category;

    @Override
    protected void sanitize() {

        if (title != null) {
            title = normalize(title);
        }

        if (description != null) {
            description = normalize(description);
        }

        if (image != null) {
            image = normalize(image);
        }
    }
}
