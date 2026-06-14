package org.bgm.productservice;

import org.bgm.productservice.controllers.ProductController;
import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.model.Product;
import org.bgm.productservice.repository.CategoryRepository;
import org.bgm.productservice.repository.ProductRepository;
import org.bgm.productservice.services.CategoryServiceImpl;
import org.bgm.productservice.services.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static  org.mockito.Mockito.*;

import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
/*
test from the controller while mocking the repositories, but then Mockito alone is not enough. You must create the entire dependency chain:
ProductController
    ↓
ProductServiceImpl
    ↓
ProductRepository (mock)

ProductController
    ↓
CategoryServiceImpl
    ↓
CategoryRepository (mock)

With pure Mockito, you need to instantiate and inject every layer.

this usually does not work reliably when there are multiple levels of @InjectMocks. Mockito does not recursively build an entire Spring dependency graph like Spring does.

 */
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private ProductController productController;

    @BeforeEach
    void setup() {
        productController = new ProductController(productService, categoryService);
    }

    @Test
    void testProductCreation() throws Exception {

        Category category = new Category();
        category.setId(1);
        category.setName("Dummy");

        when(categoryRepository.findCategoryByNameIgnoreCase("Dummy"))
                .thenReturn(Optional.of(category));

        when(productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product p = invocation.getArgument(0);
                    p.setId(100);
                    return p;
                });

        ProductDTO dto = new ProductDTO();
        dto.setTitle("title");
        dto.setCategory("Dummy");
        dto.setPrice(10);

        ProductDTO result = productController.createProduct(dto);
        Assertions.assertEquals("title", result.getTitle());
        Assertions.assertEquals("Dummy", result.getCategory());

        verify(categoryRepository, times(2))
                .findCategoryByNameIgnoreCase("Dummy");

        verify(productRepository)
                .saveAndFlush(any(Product.class));
    }
}