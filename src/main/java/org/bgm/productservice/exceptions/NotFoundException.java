package org.bgm.productservice.exceptions;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(message, null, false, false);
    }
}
