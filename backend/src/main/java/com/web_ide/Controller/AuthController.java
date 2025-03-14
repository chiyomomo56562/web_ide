package com.web_ide.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.web_ide.dto.SignupRequestDto;
import com.web_ide.dto.LoginResponseDto;
import com.web_ide.dto.LoginRequestDto;
import com.web_ide.dto.UserResponseDto;
import com.web_ide.security.jwt.JwtTokenProvider;
import com.web_ide.entity.User;
import com.web_ide.repository.UserRepository;
import com.web_ide.security.jwt.UserPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${app.jwtRefreshExpirationInMs}")
    private int jwtRefreshExpirationInMs;
	
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
	
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        // AuthenticationManager를 통해 사용자 인증 진행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 인증 성공 시, SecurityContext에 인증 정보를 저장
//        UserDetails 객체 형태로 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JwtTokenProvider를 사용해 Access Token과 Refresh Token 생성
        String accessToken = tokenProvider.generateAccessToken(authentication, "local");
        String refreshToken = tokenProvider.generateRefreshToken(authentication, "local");
        
//        httpOnly 쿠키에 refresh token 저장
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);      // HTTPS 사용 시 활성화 (개발환경에 따라 조정)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(jwtRefreshExpirationInMs/1000); 
        response.addCookie(refreshCookie);
        
//      userDetails를 implements해서 만들어져서 security context에서 user객체를 받아온다.
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
//        userResponseDto생성
        UserResponseDto userResponse = UserResponseDto.fromEntity(userPrincipal.getUser());
        
        // JwtAuthenticationResponse DTO에 토큰 정보를 담아 클라이언트에 반환
        LoginResponseDto  loginresponse   = new LoginResponseDto(accessToken, userResponse);
        logger.info("loginUser: ", userResponse.getId());
        return ResponseEntity.ok(loginresponse );
    }
    
    @GetMapping("/check-id")
    public ResponseEntity<?> checkIdAvailability(@RequestParam String id) {
        boolean exists = userRepository.existsByLoginId(id);
        return ResponseEntity.ok(Map.of("available", !exists));
    }

    // ✅ 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDto request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 아이디입니다."));
        }

        // 비밀번호 해싱 (SHA-256 대신 Bcrypt 사용)
        String hashedPwd = passwordEncoder.encode(request.getPwd());

        User user = new User(request.getLoginId(), hashedPwd, request.getEmail(), request.getNickname());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
    }
}
