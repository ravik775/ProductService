package org.bgm.productservice;

import org.bgm.productservice.controllers.CategoryController;
import org.bgm.productservice.dtos.CategoryDTO;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.repository.CategoryRepository;
import org.bgm.productservice.services.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryController categoryController;

    @BeforeEach
    void setup() {
        categoryController = new CategoryController(categoryService);
    }

    @Test
    void createCategory_returnsCreatedCategory() throws Exception {
        when(categoryRepository.findCategoryByNameIgnoreCase("Books")).thenReturn(Optional.empty());
        when(categoryRepository.saveAndFlush(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(10);
            return saved;
        });

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");
        dto.setDescription("All books");

        CategoryDTO result = categoryController.createCategory(dto).getBody();

        Assertions.assertEquals("Books", result.getName());
        Assertions.assertEquals("All books", result.getDescription());
        verify(categoryRepository).saveAndFlush(any(Category.class));
    }

    @Test
    void updateCategory_returnsUpdatedCategory() throws Exception {
        Category existing = new Category();
        existing.setId(5);
        existing.setName("Old Name");
        existing.setDescription("Old description");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findCategoryByNameIgnoreCase("New Name")).thenReturn(Optional.empty());
        when(categoryRepository.saveAndFlush(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDTO dto = new CategoryDTO();
        dto.setName("New Name");
        dto.setDescription("Updated description");

        CategoryDTO result = categoryController.updateCategory(5, dto).getBody();

        Assertions.assertEquals(5, result.getId());
        Assertions.assertEquals("New Name", result.getName());
        Assertions.assertEquals("Updated description", result.getDescription());
        verify(categoryRepository).saveAndFlush(existing);
    }
}
