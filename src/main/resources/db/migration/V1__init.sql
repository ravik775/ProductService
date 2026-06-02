CREATE TABLE product
(
    id            BIGINT       NOT NULL,
    created_by    VARCHAR(255) NOT NULL,
    updated_by    VARCHAR(255) NOT NULL,
    deleted       BIT(1)       NOT NULL,
    created_at    BIGINT       NOT NULL,
    modified_at   BIGINT       NOT NULL,
    title         VARCHAR(40)  NOT NULL,
    price         BIGINT       NOT NULL,
    `description` VARCHAR(255) NULL,
    image         VARCHAR(255) NULL,
    category_id   BIGINT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE product_category
(
    id            BIGINT       NOT NULL,
    created_by    VARCHAR(255) NOT NULL,
    updated_by    VARCHAR(255) NOT NULL,
    deleted       BIT(1)       NOT NULL,
    created_at    BIGINT       NOT NULL,
    modified_at   BIGINT       NOT NULL,
    name          VARCHAR(40)  NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_productcategory PRIMARY KEY (id)
);

ALTER TABLE product
    ADD CONSTRAINT uc_product_title UNIQUE (title);

ALTER TABLE product_category
    ADD CONSTRAINT uc_productcategory_name UNIQUE (name);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES product_category (id);