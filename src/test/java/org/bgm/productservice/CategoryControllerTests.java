package org.bgm.productservice;

import org.bgm.productservice.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class CategoryControllerTests {

    @MockitoBean
    private CategoryService categoryService;

    //@Autowired
    //private Category

    @Test
    public void test(){

    }
}
