package com.web_ide.dto;

import com.web_ide.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank
    @Size(max = 30)
    private String loginId;

    @NotBlank
    private String password;//회원가입시 비밀번호
    @NotBlank
    private String checkPassword;//회원가입시 비밀번호 확인을 위해 한번 더 입력받기?
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nickname;

    private String currentPassword;//비밀번호 바꿀때 현재 비밀번호인지 확인
    private String newPassword;//비밀번호 바꿀 사용


    public User toEntity() {
        return new User(loginId, password, email, nickname);
    }
}