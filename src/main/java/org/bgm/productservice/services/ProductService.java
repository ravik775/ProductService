package org.bgm.productservice.services;

import org.bgm.productservice.model.Product;

public interface ProductService {
    Product getProductById(String id);
}
