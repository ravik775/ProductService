package org.bgm.productservice.services;

import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.exceptions.NotFoundException;
import org.bgm.productservice.exceptions.ProductCreateException;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.bgm.productservice.model.Product;
import org.bgm.productservice.repository.CategoryRepository;
import org.bgm.productservice.repository.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("ProductService")
@Primary
@Slf4j
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class ProductServiceImpl implements ProductService{
    private static final String ENTITY_NAME = "Product";
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product getProductById(long id) throws ProductNotFoundException {
        log.debug("Fetching {} for id={}", ENTITY_NAME, id);
        var product = productRepository.findById(id);
        if(product.isEmpty()){
            log.error("{} not found for id={}", ENTITY_NAME, id);
            throw new ProductNotFoundException("Product by "+id+" not found");
        }
        log.info("Successfully fetched {} for id={}", ENTITY_NAME, id);
        return product.get();
    }

    @Override
    public Page<Product> getProducts(int page, int size) {
        var sort = Sort.by("title");
        Pageable pageabe = PageRequest.of(page, size, sort);
        var products = productRepository.findAll(pageabe);
        log.info("Fetching all {}s", ENTITY_NAME);
        return products;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Product createProduct(Product product) throws ProductCreateException {
        log.debug("Creating {}", ENTITY_NAME);

        if(product == null || product.getTitle() == null || product.getTitle().isBlank() || product.getCategory() == null || product.getCategory().getName() == null || product.getCategory().getName().isEmpty()){
            log.error("{} is empty", ENTITY_NAME);
            throw new ProductCreateException(ENTITY_NAME + " can not be null, title, category cannot be null or blank");
        }
        var cat = product.getCategory();
        var dbCat = categoryRepository.findCategoryByNameIgnoreCase(cat.getName());
        if(dbCat.isEmpty() || dbCat.get().isDeleted())
            throw new ProductCreateException("Category "+cat.getName()+" is not a valid." );
        product.setCategory(dbCat.get());
        try {
            product = productRepository.saveAndFlush(product);
            log.info("{} created successfully with id={} title={}", ENTITY_NAME, product.getId(), product.getTitle());
            return product;
        } catch (DataIntegrityViolationException ex) {
            log.error("Database constraint violation while creating {} for {}", ENTITY_NAME, product.getTitle(), ex);
            throw new ProductCreateException(String.format("Unable to create %s with title='%s' due to data constraint violation", ENTITY_NAME, product.getTitle()));
        }
    }
}
