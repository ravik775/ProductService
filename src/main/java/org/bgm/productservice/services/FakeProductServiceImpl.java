package org.bgm.productservice.services;

import org.bgm.productservice.Constants;
import org.bgm.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FakeProductServiceImpl implements ProductService {

    RestTemplate restTemplate;

    public FakeProductServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(String id) {
        return restTemplate.getForObject(Constants.FAKE_STORE_URL+"/products/"+id, Product.class);
    }
}
