package org.bgm.productservice.config;

import jakarta.servlet.http.HttpServletRequest;
import org.bgm.productservice.dtos.ErrorDTO;
import org.bgm.productservice.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDTO> productNotFound(ProductNotFoundException ex, HttpServletRequest request) {

        log.warn("Product not found at URI: {} message: {}", request.getRequestURI(), ex.getMessage()  );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        ex.getMessage(),
                        request,
                        HttpStatus.NOT_FOUND
                ));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDTO> exception(NullPointerException ex, HttpServletRequest request) {
        log.error("Unhandled exception at URI: {}", request.getRequestURI(), ex );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        "Internal server error",
                        request,
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }

    private ErrorDTO buildError(
            String message,
            HttpServletRequest request,
            HttpStatus status) {

        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setMessage(message);
        errorDTO.setStatus(status.name());
        errorDTO.setPath(request.getRequestURI());

        return errorDTO;
    }
}