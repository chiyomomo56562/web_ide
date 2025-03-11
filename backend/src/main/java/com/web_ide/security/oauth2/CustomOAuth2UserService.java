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
    
    // Kakao를 사용하므로 provider 이름은 "kakao"로 고정
    private final String PROVIDER = "kakao";

    public CustomOAuth2UserService(UserRepository userRepository, SocialAccountRepository socialAccountRepository) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
    }
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
          throws OAuth2AuthenticationException {
        // 1. 기본 로직을 통해 Kakao API로부터 사용자 정보를 JSON(Map) 형태로 받아옵니다.
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 2. Kakao API 응답에서 고유 식별자인 "id" 값을 추출합니다.
        String externalUserId = attributes.get("id").toString();
        
        // 3. 추가 사용자 정보 추출 (예: properties에서 nickname, kakao_account에서 email)
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = properties != null && properties.get("nickname") != null 
                          ? properties.get("nickname").toString() : "";
        
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = "";
        if (kakaoAccount != null && kakaoAccount.get("email") != null) {
            email = kakaoAccount.get("email").toString();
        }
        
        // 4. SocialAccountRepository를 통해, 이미 해당 소셜 계정이 매핑되어 있는지 확인합니다.
        Optional<SocialAccount> socialAccountOptional =
                socialAccountRepository.findByProviderAndExternalUserId(PROVIDER, externalUserId);
        
        User user;
        if (socialAccountOptional.isPresent()) {
            // 이미 등록된 소셜 계정이 있다면, 연결된 User 엔티티를 사용합니다.
            user = socialAccountOptional.get().getUser();
        } else {
            // 신규 사용자: 내부 User 엔티티를 생성합니다.
            // loginId는 Kakao의 고유 식별자(externalUserId)를 사용합니다.
            user = User.builder()
                     .loginId("kakao_" + externalUserId) //카카오만 사용할 예정이라 이렇게 했는데 다른 것도 추가하면 추가 로직이 필요하다.
                     .pwd("")       // 소셜 로그인은 비밀번호를 사용하지 않음
                     .email(email)
                     .nickname(nickname)
                     .build();
            userRepository.save(user);
            
            // 신규 SocialAccount 생성 및 내부 User와 연결
            SocialAccount socialAccount = SocialAccount.builder()
                .user(user)
                .provider(PROVIDER)
                .externalUserId(externalUserId)
                .accessToken(oAuth2UserRequest.getAccessToken().getTokenValue())
                // tokenExpiry는 예시로 현재 시각 기준 1시간 후로 설정
                .tokenExpiry(LocalDateTime.now().plusHours(1))
                .build();
            socialAccountRepository.save(socialAccount);
        }
        
        // 5. 최종적으로 CustomOAuth2User를 생성하여 반환합니다.
        // 이 객체는 Kakao의 전체 응답(attributes)와 내부 사용자 식별자(user.getId())를 포함합니다.
        return new CustomOAuth2User(attributes, user.getId());
    }
}