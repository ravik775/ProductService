package org.bgm.productservice.controllers;
import lombok.AllArgsConstructor;
import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") String id){
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ProductDTO.fromProduct(product));
    }
}
