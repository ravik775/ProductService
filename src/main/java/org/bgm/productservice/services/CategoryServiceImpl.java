package org.bgm.productservice.services;

import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.exceptions.CreationException;
import org.bgm.productservice.exceptions.NotFoundException;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.repository.CategoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("ProductCategory")
@Primary
@Slf4j
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private static final String ENTITY_NAME = "Category";

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(long id) throws NotFoundException {
        log.debug("Fetching {} for id={}", ENTITY_NAME, id);
        var obj = categoryRepository.findById(id);
        if (obj.isEmpty() || obj.get().isDeleted()) {
            throw new NotFoundException(ENTITY_NAME + " by " + id + " not found");
        }
        log.info("Successfully fetched {} for id={}", ENTITY_NAME, id);
        return obj.get();
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Category createCategory(Category category) throws CreationException {
        log.debug("Creating {}", ENTITY_NAME);
        validateCategoryForCreate(category);

        var existing = categoryRepository.findCategoryByNameIgnoreCase(category.getName());
        if (existing.isPresent() && !existing.get().isDeleted()) {
            throw new CreationException(ENTITY_NAME + " with name '" + category.getName() + "' already exists");
        }

        try {
            category = categoryRepository.saveAndFlush(category);
            log.info("{} created successfully with id={} name={}", ENTITY_NAME, category.getId(), category.getName());
            return category;
        } catch (DataIntegrityViolationException ex) {
            log.error("Database constraint violation while creating {} for {}", ENTITY_NAME, category.getName(), ex);
            throw new CreationException("Unable to create " + ENTITY_NAME + " with name='" + category.getName()
                    + "' due to data constraint violation");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Category updateCategory(long id, Category category) throws NotFoundException, CreationException {
        log.debug("Updating {} for id={}", ENTITY_NAME, id);
        validateCategoryForUpdate(category);

        Category existing = getCategoryById(id);

        var duplicate = categoryRepository.findCategoryByNameIgnoreCase(category.getName());
        if (duplicate.isPresent()
                && duplicate.get().getId() != id
                && !duplicate.get().isDeleted()) {
            throw new CreationException(ENTITY_NAME + " with name '" + category.getName() + "' already exists");
        }

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());

        try {
            existing = categoryRepository.saveAndFlush(existing);
            log.info("{} updated successfully with id={} name={}", ENTITY_NAME, existing.getId(), existing.getName());
            return existing;
        } catch (DataIntegrityViolationException ex) {
            log.error("Database constraint violation while updating {} for id={}", ENTITY_NAME, id, ex);
            throw new CreationException("Unable to update " + ENTITY_NAME + " with id=" + id
                    + " due to data constraint violation");
        }
    }

    @Override
    public Page<Category> getAllCategory(int page, int size) {
        var pageRequest = PageRequest.of(page, size, Sort.by("name"));
        return categoryRepository.findAll(pageRequest);
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findCategoryByNameIgnoreCase(name);
    }

    private void validateCategoryForCreate(Category category) throws CreationException {
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            throw new CreationException(ENTITY_NAME + " name cannot be null or blank");
        }
        if (category.getName().length() > 40) {
            throw new CreationException(ENTITY_NAME + " name cannot exceed 40 characters");
        }
    }

    private void validateCategoryForUpdate(Category category) throws CreationException {
        validateCategoryForCreate(category);
    }
}
