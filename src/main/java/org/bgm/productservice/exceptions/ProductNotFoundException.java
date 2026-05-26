package org.bgm.productservice.exceptions;

public class ProductNotFoundException extends Exception{
    public ProductNotFoundException(String message){
        super(message, null, false, false); // super(message, cause, enableSuppression, writableStackTrace);
    }
}
