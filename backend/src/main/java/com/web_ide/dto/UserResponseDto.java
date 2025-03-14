package com.web_ide.dto;

import com.web_ide.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
	//id가 의미 있는 값이네....
    private Long id;
    private String loginId;
    private String nickname;
    private String email;

//    public UserResponseDto(User user) {
//        this.id = user.getId();
//        this.loginId = user.getLoginId();
//        this.nickname = user.getNickname();
//        this.email = user.getEmail();
//    }

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getEmail()
            );
    }
}
