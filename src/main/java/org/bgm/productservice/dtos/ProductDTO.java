package org.bgm.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.model.Product;

@Setter
@Getter
public class ProductDTO {
    private long id;
    private String title;
    private long price;
    private String description;
    private String image;
    private String categoryName;

    public Product toProduct() {
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        product.setTitle(title);
        product.setPrice(price);
        product.setImage(image);
        return product;
    }

    public static ProductDTO fromProduct(Product product) {
        var dto = new ProductDTO();
        dto.setDescription(product.getDescription());
        dto.setTitle(product.getTitle());
        dto.setId((product.getId()));
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ProductDTO)) return false;
        final ProductDTO other = (ProductDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        if (this.getPrice() != other.getPrice()) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$image = this.getImage();
        final Object other$image = other.getImage();
        if (this$image == null ? other$image != null : !this$image.equals(other$image)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProductDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final long $price = this.getPrice();
        result = result * PRIME + (int) ($price >>> 32 ^ $price);
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $image = this.getImage();
        result = result * PRIME + ($image == null ? 43 : $image.hashCode());
        return result;
    }

    public String toString() {
        return "ProductDTO(id=" + this.getId() + ", title=" + this.getTitle() + ", price=" + this.getPrice() + ", description=" + this.getDescription() + ", image=" + this.getImage() + ")";
    }
}
