package org.bgm.productservice.controllers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.bgm.productservice.dtos.ErrorDTO;
import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") String id) throws ProductNotFoundException {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ProductDTO.fromProduct(product));
    }

    @RequestMapping(value="/products/", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDTO>> getProducts(){
        var products = productService.getProducts();
        ArrayList<ProductDTO> productDtos= new ArrayList<>(products.size());
        for(var product : products){
            productDtos.add(ProductDTO.fromProduct(product));
        }
        return ResponseEntity.ok(productDtos);
    }

    @PostMapping("/product")
    public ProductDTO createProduct(@RequestBody ProductDTO product){
        var newProduct = productService.createProduct(product.toProduct());
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
