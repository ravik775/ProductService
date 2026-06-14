package org.bgm.productservice;

import org.bgm.productservice.controllers.ProductController;
import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.model.Product;
import org.bgm.productservice.repository.CategoryRepository;
import org.bgm.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {
/*
If you want real services + mocked repositories
ProductController
    -> ProductServiceImpl
         -> mocked ProductRepository

    -> CategoryServiceImpl
         -> mocked CategoryRepository

 */
    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @Test
    @WithMockUser(authorities = "Admin")
    void testProductCreation() throws Exception {

        Category category = new Category();
        category.setId(1);
        category.setName("Dummy");

        when(categoryRepository.findCategoryByNameIgnoreCase("Dummy"))
                .thenReturn(Optional.of(category));

        when(categoryRepository.findCategoryByNameIgnoreCase(argThat(
                name -> !"Dummy".equalsIgnoreCase(name))))
                .thenReturn(Optional.empty());

        when(productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(100);
                    return saved;
                });

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(100);
        productDTO.setTitle("title");
        productDTO.setDescription("Description");
        productDTO.setImage("image");
        productDTO.setPrice(10);
        productDTO.setCategory("Dummy");

        ProductDTO newProduct = productController.createProduct(productDTO);

        Assertions.assertEquals("title", newProduct.getTitle());
        Assertions.assertEquals("Dummy", newProduct.getCategory());

        verify(categoryRepository, times(2))
                .findCategoryByNameIgnoreCase("Dummy");

        verify(productRepository)
                .saveAndFlush(any(Product.class));
    }
}