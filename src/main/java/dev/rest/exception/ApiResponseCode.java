package dev.rest.exception;

import lombok.Getter;

@Getter
public enum ApiResponseCode {

    OK(200, "R20000", "정상"),
    BAD_REQUEST(400, "R40000", "비정상적인 요청입니다."),
    // INVALID_INPUT_VALUE(400, "R40001", "유효하지 않은 값입니다."),
    // USER_SIGNUP_FAILURE(400, "R40010-003", "가입이 불가능합니다."),
    UNAUTHORIZED(401, "R40100", "인증이 필요한 API 입니다."),
    ACCESS_DENIED(403, "R40300", "접근이 허용되지 않습니다."),
    DATA_NOT_FOUND(404, "R40402", "데이터가 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "R50000", "처리 중 오류가 발생했습니다.");

    private final String code;
    private final String message;
    private final int status;

    ApiResponseCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
