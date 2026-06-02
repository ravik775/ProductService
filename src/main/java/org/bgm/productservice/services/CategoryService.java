package org.bgm.productservice.services;

import org.bgm.productservice.exceptions.CreationException;
import org.bgm.productservice.exceptions.NotFoundException;
import org.bgm.productservice.model.Category;
import org.springframework.data.domain.Page;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category getCategoryById(long id) throws NotFoundException;
    Category createCategory(Category category) throws CreationException;
    Page<Category> getAllCategory(int page, int size);
    Optional<Category> findCategoryByName(String name);
}
