package dev.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Map;

@Schema(description = "성공 응답 DTO")
public record ProductPageResponse(
        @ArraySchema(
                schema = @Schema(implementation = ProductResponse.class),
                arraySchema = @Schema(description = "상품 응답 목록")
        )
        List<ProductResponse> products,

        @Schema(
                description = "HATEOAS 링크 목록",
                // Link는 HATEOAS 라이브러리 자체 API 타입이기 때문에 스키마 자동 추론이 복잡하여 example로 직접 명시
                example = """
        {
          "self": { "href": "/api/products", "type": "POST", "templated": false },
          "list-products": { "href": "/api/products?page=0&size=10{&category}", "type": "GET", "templated": true },
          "update-product": { "href": "/api/products/13", "type": "PUT", "templated": false },
          "delete-product": { "href": "/api/products/13", "type": "DELETE", "templated": false }
        }
        """
        )
        @JsonProperty("_links")
        Map<String, Link> links
) {}
