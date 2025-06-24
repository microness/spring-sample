package dev.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rest.config.JwtTokenProvider;
import dev.rest.config.SecurityConfig;
import dev.rest.dto.LoginRequest;
import dev.rest.dto.LoginResponse;
import dev.rest.dto.SignupRequest;
import dev.rest.dto.SignupResponse;
import dev.rest.service.AuthService;
import dev.rest.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@DisplayName("인증 컨트롤러 테스트")
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // @MockBean은 Spring boot 3.4부터 removal
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("회원가입 API 호출시 201 상태코드와 함께 응답이 반환되어야 한다")
    void given_signup_request_when_post_signup_then_returns_201() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("testuser", "password123", "test@email.com");
        SignupResponse mockResponse = new SignupResponse(1L, "testuser", "test@email.com");
        given(authService.signup(any(SignupRequest.class))).willReturn(mockResponse);

        // When
        MvcTestResult result = mockMvcTester.perform(post("/api/auth/signup").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(SignupResponse.class)
                        .satisfies(signupResponse -> {
                            assertThat(signupResponse.username()).isEqualTo("testuser");
                            assertThat(signupResponse.email()).isEqualTo("test@email.com");
                        });
    }


    @Test
    @DisplayName("로그인 API 호출시 200 상태코드와 함께 JWT 토큰이 반환되어야 한다")
    void given_login_request_when_post_login_then_returns_200_with_token() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        String token = "mock-jwt-token";

        given(authService.login(any(LoginRequest.class))).willReturn(token);

        // When
        MvcTestResult result = mockMvcTester.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(LoginResponse.class)
                .satisfies(loginResponse -> {
                    assertThat(loginResponse.accessToken()).isEqualTo(token);
                });
    }
}