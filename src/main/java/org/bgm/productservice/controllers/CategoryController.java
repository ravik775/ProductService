package org.bgm.productservice.controllers;

import org.bgm.productservice.dtos.CategoryDTO;
import org.bgm.productservice.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService service){
        this.categoryService = service;
    }

    @GetMapping("/category/")
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@RequestParam(name="page", defaultValue = "1") int page,
                                                              @RequestParam(name="size", defaultValue = "20") int size){
        var categories = categoryService.getAllCategory(page, size);
        ArrayList<CategoryDTO> dtos = new ArrayList<>(categories.getSize());
        for(var cat : categories)
        {
            dtos.add(CategoryDTO.from(cat));
        }
        return ResponseEntity.ok(dtos);
    }
}
