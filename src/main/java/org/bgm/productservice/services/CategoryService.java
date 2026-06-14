package org.bgm.productservice.services;

import org.bgm.productservice.exceptions.CreationException;
import org.bgm.productservice.exceptions.NotFoundException;
import org.bgm.productservice.model.Category;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CategoryService {
    Category getCategoryById(long id) throws NotFoundException;
    Category createCategory(Category category) throws CreationException;
    Category updateCategory(long id, Category category) throws NotFoundException, CreationException;
    Page<Category> getAllCategory(int page, int size);
    Optional<Category> findCategoryByName(String name);
}
