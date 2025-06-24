package dev.rest.service;

import dev.rest.config.JwtTokenProvider;
import dev.rest.dto.LoginRequest;
import dev.rest.dto.SignupRequest;
import dev.rest.dto.SignupResponse;
import dev.rest.model.User;
import dev.rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        User user = User.create(request, passwordEncoder);

        User saved = userRepository.save(user);
        return SignupResponse.from(saved);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generateToken(user.getId());
    }
}
