package org.bgm.productservice.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.bgm.productservice.dtos.ErrorDTO;
import org.bgm.productservice.exceptions.CreationException;
import org.bgm.productservice.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {

        log.warn("Resource not found. uri={} message={}", request.getRequestURI(), ex.getMessage());

        return buildResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CreationException.class)
    public ResponseEntity<ErrorDTO> handleCreationException(CreationException ex, HttpServletRequest request) {

        log.warn("Resource creation failed. uri={} message={}", request.getRequestURI(), ex.getMessage());

        return buildResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorDTO> handleAccessDenied(Exception ex, HttpServletRequest request) {

        log.warn("Access denied. uri={} message={}", request.getRequestURI(), ex.getMessage());

        return buildResponse("Access denied", request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception. uri={}", request.getRequestURI(), ex);

        return buildResponse("Internal server error", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDTO> buildResponse(String message, HttpServletRequest request, HttpStatus status) {

        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setMessage(message);
        errorDTO.setStatus(status.name());
        errorDTO.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(errorDTO);
    }
}