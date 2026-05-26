package org.bgm.productservice.services;

import org.bgm.productservice.Constants;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class FakeProductServiceImpl implements ProductService {

    RestTemplate restTemplate;

    public FakeProductServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(String id) throws ProductNotFoundException {
        var product = restTemplate.getForObject(Constants.FAKE_STORE_URL+"/products/"+id, Product.class);
        if(product == null)
            throw new ProductNotFoundException("Product "+id+" not found.");
        return product;
    }

    @Override
    public List<Product> getProducts(){
        Product[] products = restTemplate.getForObject(Constants.FAKE_STORE_URL + "/products/", Product[].class );
        return products != null ? Arrays.asList(products) : Collections.emptyList();
    }

    @Override
    public Product createProduct(Product product) {
        product = restTemplate.postForObject(Constants.FAKE_STORE_URL+"/products", product, Product.class);
        return product;
    }
}
