package org.bgm.productservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Product {
    private long id;
    private String title;
    private long price;
    private String description;
    private String image;
    private String category;
}
