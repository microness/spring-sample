package dev.rest.dto;

import dev.rest.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResponse(

        Long id,

        String name,

        String description,

        int price,

        int stock,

        String category


) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory()
        );
    }
}
