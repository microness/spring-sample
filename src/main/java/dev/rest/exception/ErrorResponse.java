package dev.rest.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;

@Builder
@Getter
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "400")
    private String code;

    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private String message;

    @Schema(description = "필드 에러 목록")
    private List<FieldError> errors;

    @Getter
    @Builder
    public static class FieldError {
        private String field;
        private Object value;
        private String reason;
    }

    public static ErrorResponse fromBindingResult(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue())
                        .reason(error.getDefaultMessage())
                        .build())
                .toList();

        return ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .errors(fieldErrors)
                .build();
    }
}
