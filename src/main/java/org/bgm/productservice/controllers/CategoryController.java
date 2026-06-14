package org.bgm.productservice.controllers;

import org.bgm.productservice.dtos.CategoryDTO;
import org.bgm.productservice.security.HasAuthority;
import org.bgm.productservice.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService service) {
        this.categoryService = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/category/", "/catalog/"})
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        var categories = categoryService.getAllCategory(page, size);
        ArrayList<CategoryDTO> dtos = new ArrayList<>(categories.getSize());
        for (var cat : categories) {
            dtos.add(CategoryDTO.from(cat));
        }
        return ResponseEntity.ok(dtos);
    }

    @HasAuthority("Admin")
    @PostMapping({"/category", "/catalog"})
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) throws Exception {
        var category = categoryService.createCategory(categoryDTO.toCategory());
        return ResponseEntity.ok(CategoryDTO.from(category));
    }

    @HasAuthority("Admin")
    @PutMapping({"/category/{id}", "/catalog/{id}"})
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable("id") long id,
            @RequestBody CategoryDTO categoryDTO) throws Exception {
        var category = categoryService.updateCategory(id, categoryDTO.toCategory());
        return ResponseEntity.ok(CategoryDTO.from(category));
    }
}
