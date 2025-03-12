package com.web_ide.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String loginId; //로그인 할 떄 쓰는 id

    @Column(nullable = true)
    private String pwd; //비번

    @Column(nullable = true, unique = true)
    private String email; //이메일

    @Column(nullable = false, length = 20)
    private String nickname; //닉네임

    @Builder
    public User(String loginId, String pwd, String email, String nickname) {
        this.loginId = loginId;
        this.pwd = pwd;
        this.email = email;
        this.nickname = nickname;
    }

    public void updateNickname(String nickname) {
        if(nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }else{
            throw new IllegalArgumentException("바꾸실 닉네임을 입력해주세요");
        }
    }

    public void updatePwd(String pwd) {
        if(pwd != null && !pwd.isBlank()) {
            this.pwd = pwd;
        }else{
            throw new IllegalArgumentException("바꿀 비밀번호를 입력해주세요");
        }
    }
    
//    getter가 있는데 ide에서 계속 빨간줄 나와서 넣었습니다.
    public Long getId() {
        return this.id;
    }

}
