package com.web_ide.security.oauth2;

import com.web_ide.entity.SocialAccount;
import com.web_ide.entity.User;
import com.web_ide.repository.SocialAccountRepository;
import com.web_ide.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    // 소셜 제공자 이름 상수 (Kakao의 경우 "kakao")
    private final String PROVIDER = "kakao";

    public CustomOAuth2UserService(UserRepository userRepository, SocialAccountRepository socialAccountRepository) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
            throws OAuth2AuthenticationException {
        // Kakao API로부터 기본 사용자 정보를 로드
        OAuth2User oauth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Kakao API의 응답에서 "id"를 추출 (고유 식별자)
        String externalUserId = attributes.get("id").toString();

        // 추가 정보 추출: properties -> nickname
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = properties != null && properties.get("nickname") != null
                ? properties.get("nickname").toString() : "";
        // kakao_account -> email
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = "";
        if (kakaoAccount != null && kakaoAccount.get("email") != null) {
            email = kakaoAccount.get("email").toString();
        }

        // SocialAccount을 통해 내부 매핑 확인
        Optional<SocialAccount> socialAccountOptional =
                socialAccountRepository.findByProviderAndExternalUserId(PROVIDER, externalUserId);

        User user;
        if (socialAccountOptional.isPresent()) {
            // 이미 등록된 소셜 계정이면 연결된 User 사용
            user = socialAccountOptional.get().getUser();
        } else {
            // 신규 소셜 계정이면, User 엔티티 신규 생성 (loginId는 외부UserId 사용)
            user = User.builder()
                    .loginId(externalUserId)      // 외부 고유 식별자를 내부 loginId로 사용
                    .pwd("")                      // 소셜 로그인에서는 비밀번호를 사용하지 않음
                    .email(email)
                    .nickname(nickname)
                    .build();
            userRepository.save(user);

            // 신규 SocialAccount 생성
            SocialAccount socialAccount = new SocialAccount();
            // 연관 관계 설정 (SocialAccount -> User)
            // setter나 builder를 사용 (여기서는 setter로 간단하게 처리)
            // socialAccount.setUser(user);
            // 아래와 같이 직접 필드에 접근할 수 없으므로, SocialAccount 엔티티에 setter 또는 builder가 있어야 합니다.
            // 예시로 builder를 사용하는 방식:
            socialAccount = SocialAccount.builder()
                    .user(user)
                    .provider(PROVIDER)
                    .externalUserId(externalUserId)
                    .accessToken(oAuth2UserRequest.getAccessToken().getTokenValue())
                    // tokenExpiry는 적절한 만료시간으로 설정 (여기서는 현재 시각 + 1시간을 예시로)
                    .tokenExpiry(LocalDateTime.now().plusHours(1))
                    .build();
            socialAccountRepository.save(socialAccount);
        }

        // 최종적으로 CustomOAuth2User를 생성하여 반환 (내부 User의 id와 Kakao의 응답 attributes 포함)
        return new CustomOAuth2User(attributes, user.getId());
    }
}