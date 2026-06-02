package org.bgm.productservice.services;

import org.bgm.productservice.Constants;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service("FakeProductService")
public class FakeProductServiceImpl implements ProductService {

    RestTemplate restTemplate;
    public FakeProductServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(long id) throws ProductNotFoundException {
        var product = restTemplate.getForObject(Constants.FAKE_STORE_URL+"/products/"+id, Product.class);
        if(product == null)
            throw new ProductNotFoundException("Product "+id+" not found.");
        return product;
    }

    @Override
    public Page<Product> getProducts(int page, int size) {
        Product[] products = restTemplate.getForObject(Constants.FAKE_STORE_URL + "/products/", Product[].class );
        Pageable pageable = PageRequest.of(page, size);
        if(products == null)
            products = new Product[0];
        return new PageImpl<>(Arrays.asList(products), pageable, products.length);
    }

    @Override
    public Product createProduct(Product product) {
        product = restTemplate.postForObject(Constants.FAKE_STORE_URL+"/products", product, Product.class);
        return product;
    }



}
