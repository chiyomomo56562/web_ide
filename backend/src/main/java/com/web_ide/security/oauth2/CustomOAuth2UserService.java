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
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;


@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = Logger.getLogger(CustomOAuth2UserService.class.getName());

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final String PROVIDER = "kakao";
    
    @PersistenceContext
    private EntityManager entityManager;

    public CustomOAuth2UserService(UserRepository userRepository, SocialAccountRepository socialAccountRepository) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        logger.info("🔹 요청된 클라이언트 ID: " + oAuth2UserRequest.getClientRegistration().getClientId());
        logger.info("🔹 요청된 redirect URI: " + oAuth2UserRequest.getClientRegistration().getRedirectUri());
        logger.info("🔹 요청된 토큰 URI: " + oAuth2UserRequest.getClientRegistration().getProviderDetails().getTokenUri());

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        logger.info("🔹🔹attributes: "+ attributes);
        
        Long externalUserId = ((Number) attributes.get("id")).longValue();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : "";
        logger.info("🔹externalUserId :"+externalUserId);

        Optional<SocialAccount> socialAccountOptional = socialAccountRepository.findByProviderAndExternalUserId(PROVIDER, externalUserId.toString());

        User user;
        if (socialAccountOptional.isPresent()) {
            user = socialAccountOptional.get().getUser();
            
            // ✅ 닉네임만 업데이트
            if (!user.getNickname().equals(nickname) && !nickname.isBlank()) {
                user.updateNickname(nickname);
                user = entityManager.merge(user); // ✅ 영속 상태로 변환
                userRepository.saveAndFlush(user); // ✅ 즉시 저장
            }
        } else {
            user = User.builder()
                     .loginId(PROVIDER + "_" + externalUserId)
                     .pwd(null) // ✅ 소셜 로그인에서는 비밀번호를 사용하지 않음
                     .nickname(nickname)
                     .build();
            userRepository.save(user);
            
            user = entityManager.merge(user); // ✅ 영속 상태로 변환
            userRepository.saveAndFlush(user); // ✅ 즉시 저장

            SocialAccount socialAccount = SocialAccount.builder()
                .user(user)
                .provider(PROVIDER)
                .externalUserId(externalUserId.toString())
                .build();
            socialAccountRepository.save(socialAccount); // ✅ 즉시 저장
        }
        logger.info("🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹🔹");
        return new CustomOAuth2User(attributes, user.getId());
    }
}