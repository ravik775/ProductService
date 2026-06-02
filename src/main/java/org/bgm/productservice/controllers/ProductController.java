package org.bgm.productservice.controllers;

import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.services.CategoryService;
import org.bgm.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // @Qualifier("ProductService")
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id) throws Exception {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ProductDTO.fromProduct(product));
    }

    @RequestMapping(value = "/products/", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDTO>> getProducts(@RequestParam(name="page", defaultValue = "1") int page,
                                                        @RequestParam(name="size", defaultValue = "20") int size) {
        var products = productService.getProducts(page, size);
        ArrayList<ProductDTO> productDtos = new ArrayList<>(products.getNumberOfElements());
        for (var product : products) {
            productDtos.add(ProductDTO.fromProduct(product));
        }
        return ResponseEntity.ok(productDtos);
    }

    @PostMapping("/product")
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) throws Exception {
        var product = productDTO.toProduct();
        var cat = categoryService.findCategoryByName(productDTO.getCategoryName());
        cat.ifPresent(product::setCategory);
        var newProduct = productService.createProduct(product);
        return ProductDTO.fromProduct(newProduct);
    }

    /*
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDTO> handleNullPointer(NullPointerException ex, HttpServletRequest request){

        //log.error("NullPointerException occurred at URI: {}", request.getRequestURI(), ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Null Pointer Exception, Please contact administrator");
        errorDTO.setStatus("Failure");
        errorDTO.setPath(request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDTO);
    }*/
}
