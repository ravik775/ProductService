package org.bgm.productservice.repository;

import org.bgm.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByTitleIgnoreCase(String title);
    Page<Product> findByCategory_Name(String name, Pageable pageable);

    @Query("Select p from Product p where p.category.name=:name") //JPQL query
    List<Product> findByCategoryName(@Param("name") String name);
/*
    //Native
    @Query(value = "SELECT p.* FROM product p INNER JOIN ProductCategory c ON p.category_id = c.id WHERE c.name = :name",
            nativeQuery = true)
    List<Product> findByCategoryNameNative(@Param("name") String name);
*/

}
