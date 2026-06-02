package org.bgm.productservice.services;

import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.exceptions.CreationException;
import org.bgm.productservice.exceptions.NotFoundException;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.repository.CategoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ProductCategory")
@Primary
@Slf4j
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private static final String ENTITY_NAME="Category";

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(long id) throws NotFoundException {
        log.debug("Fetching {} for id={}", ENTITY_NAME, id);
        var obj = categoryRepository.findById(id);
        if(obj.isEmpty())
            throw new NotFoundException("Product by "+id+" not found");
        log.info("Successfully fetched {} for id={}", ENTITY_NAME, id);
        return obj.get();
    }

    @Override
    public Category createCategory(Category category) throws CreationException {
        return null;
    }

    @Override
    public Page<Category> getAllCategory(int page, int size) {
        var pageRequest = PageRequest.of(page, size, Sort.by("name"));
        var pageData = categoryRepository.findAll(pageRequest);
        return pageData;
    }

    @Override
    public Optional<Category> findCategoryByName(String name){
        return categoryRepository.findCategoryByNameIgnoreCase(name);
    }

}
