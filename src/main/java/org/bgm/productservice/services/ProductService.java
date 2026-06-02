package org.bgm.productservice.services;

import org.bgm.productservice.exceptions.ProductCreateException;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product getProductById(long id) throws ProductNotFoundException;
    Page<Product> getProducts(int page, int size);
    Product createProduct(Product product) throws ProductCreateException;
}
