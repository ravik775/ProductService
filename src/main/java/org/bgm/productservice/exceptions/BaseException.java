package org.bgm.productservice.exceptions;

public class BaseException extends Exception {
    public BaseException(String message) {
        super(message);
    }
    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message,  cause, enableSuppression, writableStackTrace);
    }
}
