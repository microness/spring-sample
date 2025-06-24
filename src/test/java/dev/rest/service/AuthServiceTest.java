package dev.rest.service;

import dev.rest.config.JwtTokenProvider;
import dev.rest.dto.LoginRequest;
import dev.rest.dto.SignupRequest;
import dev.rest.dto.SignupResponse;
import dev.rest.model.User;
import dev.rest.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    AuthService authService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTests {

        @Test
        @DisplayName("정상적인 회원가입 요청시 사용자가 생성되어야 한다")
        void given_valid_signup_request_when_signup_then_user_created() {
            SignupRequest request = new SignupRequest("testuser", "password123", "test@email.com");
            given(userRepository.existsByUsername("testuser")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encoded-password");

            User savedUser = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("encoded-password")
                    .email("test@email.com")
                    .build();

            given(userRepository.save(any(User.class))).willReturn(savedUser);

            SignupResponse response = authService.signup(request);

            assertThat(response.username()).isEqualTo("testuser");
            assertThat(response.email()).isEqualTo("test@email.com");
        }

        @Test
        @DisplayName("사용자 이름이 이미 존재할 경우, 예외가 발생한다")
        void given_existing_username_when_signup_then_throws_exception() {
            SignupRequest request = new SignupRequest("testuser", "password123", "test@email.com");
            given(userRepository.existsByUsername("testuser")).willReturn(true);

            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용 중인 사용자 이름입니다.");
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTests {
        @Test
        @DisplayName("존재하지 않는 사용자로 로그인할 경우 예외가 발생한다.")
        void given_nonexistent_user_when_login_then_throws_exception() {
            LoginRequest request = new LoginRequest("testuser", "password123");
            // userRepository에서 해당 사용자 못 찾음 (Optional.empty() 반환)
            given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());

            // 로그인 시도 시 RuntimeException 발생 예상
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인할 경우 예외가 발생한다.")
        void given_wrong_password_when_login_then_throws_exception() {
            LoginRequest request = new LoginRequest("testuser", "잘못된 비밀번호");

            // 모킹용 정상 사용자 정보 데이터 구성
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("encoded-password") // 암호화된 패스워드
                    .build();
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

            // 비밀번호 검증 실패하도록 설정
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");
        }

        @Test
        @DisplayName("올바른 로그인 정보로 로그인시 JWT 토큰이 반환되어야 한다")
        void given_valid_login_request_when_login_then_returns_jwt_token() {
            LoginRequest request = new LoginRequest("testuser", "password123");
            // 모킹용 정상 사용자 정보 데이터 구성
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("encoded-password")
                    .build();

            // 로그인이 성공했다고 모킹
            given(userRepository.findByUsername(request.username())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);

            // jwtTokenProvider의 generateToken 메서드가 "mock-jwt-token" 반환하도록 설정
            given(jwtTokenProvider.generateToken(user.getId())).willReturn("mock-jwt-token");

            String token = authService.login(request);

            assertThat(token).isNotNull();
            assertThat(token).isEqualTo("mock-jwt-token");
        }
    }
}