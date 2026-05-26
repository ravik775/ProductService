package org.bgm.productservice.services;

import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.model.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(String id) throws ProductNotFoundException;
    List<Product> getProducts();
    Product createProduct(Product product);
}
