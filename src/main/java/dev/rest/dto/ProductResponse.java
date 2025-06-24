package dev.rest.dto;

import dev.rest.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 응답 DTO")
public record ProductResponse(

        @Schema(description = "상품 ID", example = "1")
        Long id,

        @Schema(description = "상품 이름", example = "맥북 프로 16인치")
        String name,

        @Schema(description = "상품 설명", example = "고성능 M칩을 탑재한 전문가용 노트북")
        String description,

        @Schema(description = "상품 가격", example = "3500000")
        int price,

        @Schema(description = "상품 재고 수량", example = "5")
        int stock,

        @Schema(description = "상품 카테고리", example = "전자제품")
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
