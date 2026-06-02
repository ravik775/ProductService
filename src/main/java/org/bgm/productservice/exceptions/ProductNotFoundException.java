package org.bgm.productservice.exceptions;

public class ProductNotFoundException extends NotFoundException{
    public ProductNotFoundException(String message){
        super(message); // super(message, cause, enableSuppression, writableStackTrace);
    }
}
