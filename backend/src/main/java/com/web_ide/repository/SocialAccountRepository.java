package com.web_ide.repository;

import com.web_ide.entity.SocialAccount;
import com.web_ide.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    boolean existsByUser(User user);
    Optional<SocialAccount> findByProviderAndExternalUserId(String provider, String externalUserId);
}
