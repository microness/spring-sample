package dev.rest.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super(ApiResponseCode.DATA_NOT_FOUND.getMessage());
    }
}
