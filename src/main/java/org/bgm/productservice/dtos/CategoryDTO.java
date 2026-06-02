package org.bgm.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.bgm.productservice.model.Category;

@Getter
@Setter
public class CategoryDTO {
    private long id;
    private String name;
    private String description;

    public static CategoryDTO from(Category category){
        if(category == null)
            return null;
        var dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public Category from(){
        var category = new Category();
        category.setDescription(description);
        category.setName(name);
        category.setId(id);
        return category;
    }
}
