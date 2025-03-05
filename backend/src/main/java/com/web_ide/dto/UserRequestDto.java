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
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nickname;

    public User toEntity() {
        return new User(loginId, password, email, nickname);
    }
}