package com.web_ide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "social_accounts")
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne(fetch = FetchType.LAZY)
    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true)//일단 카카오 로그인 하나만 하니까
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String provider;

    @Column(name = "external_user_id", nullable = false)
    private String externalUserId;

}
