package org.bgm.productservice.dtos;

import lombok.Data;
import org.bgm.productservice.model.Product;

@Data
public class ProductDTO {
    private long id;
    private String title;
    private long price;
    private String description;
    private String image;

    public Product toProduct(){
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        product.setTitle(title);
        product.setPrice(price);
        product.setImage(image);
        return product;
    }

    public static ProductDTO fromProduct(Product product){
        var dto = new ProductDTO();
        dto.setDescription(product.getDescription());
        dto.setTitle(product.getTitle());
        dto.setId((product.getId()));
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
