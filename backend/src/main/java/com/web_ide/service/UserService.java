package com.web_ide.service;

import com.web_ide.dto.UserRequestDto;
import com.web_ide.dto.UserResponseDto;
import com.web_ide.entity.User;
import com.web_ide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto registerUser(UserRequestDto requestDto) {
        // 중복 아이디 , 이메일 검사
        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        // DTO -> Entity 변환 후 저장
        User user = userRepository.save(requestDto.toEntity());
        // Entity -> DTO 변환 후 반환
        return UserResponseDto.fromEntity(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    public UserResponseDto updateUser(Long userId, UserRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.updateNickname(requestDto.getNickname()); // 닉네임 변경
        user.updatePwd(requestDto.getPassword()); // 비밀번호 변경

        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(updatedUser);
    }
}