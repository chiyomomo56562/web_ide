package com.web_ide.dto;

import com.web_ide.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
	//id를 왜 만든거지? 의미가 있나?
    private Long id;
    private String loginId;
    private String nickname;
    private String email;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
    }

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(user);
    }
}
