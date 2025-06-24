package dev.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 등록 요청 DTO")
public record ProductRequest(

        @Schema(description = "상품명", example = "아이폰 15 Pro", required = true)
        @NotBlank(message = "상품 이름은 필수입니다.") // 공백, 빈문자열 허용 x
        String name,

        @Schema(description = "상품 설명", example = "최신 A17 칩이 탑재된 스마트폰", required = true)
        @NotBlank(message = "상품 설명은 필수입니다.")
        @Size(max = 1000, message = "상품 설명은 1000자 이하로 입력해주세요.") // 설명 문자열 길이 제한
        String description,

        @Schema(description = "가격 (원 단위)", example = "1350000", minimum = "0", required = true)
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.") // 음수 방지
        int price,

        @Schema(description = "재고 수량", example = "10", minimum = "1", required = true)
        @Min(value = 0, message = "재고는 1개 이상이어야 합니다.")
        int stock,

        @Schema(description = "카테고리명", example = "전자제품", required = true)
        @NotBlank(message = "카테고리는 필수입니다.")
        String category

) {}