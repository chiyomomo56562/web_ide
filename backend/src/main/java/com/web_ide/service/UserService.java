package com.web_ide.service;

import com.web_ide.dto.UserRequestDto;
import com.web_ide.dto.UserResponseDto;
import com.web_ide.entity.User;
import com.web_ide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto registerUser(UserRequestDto requestDto) {
        // 중복 아이디 , 이메일 검사
        if (checkDuplicateLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        if (checkDuplicateEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        //비밀번호는 8자 이상 , 특수문자 한개 이상 포함
        if (!isValidPassword(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 8자이상이여야하고 특수문자가 하나 이상 포함되어야 합니다.");
        }
        //입력한 비밀번호 확인하기
        if (!requestDto.getPassword().equals(requestDto.getCheckPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(requestDto.getLoginId() , encodedPassword , requestDto.getEmail() , requestDto.getNickname());
        userRepository.save(user);

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

        // 닉네임 변경
        if (requestDto.getNickname() != null) {
            user.updateNickname(requestDto.getNickname());
        }

        //비밀번호 확인하고 비밀번호 바꾸기
        if (requestDto.getCurrentPassword() != null && requestDto.getNewPassword() != null) {
            // 현재 비밀번호 확인하고 비밀번호가 맞아야 수정가능
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPwd())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            // 새 비밀번호가 원래 비밀번호와 동일한지 검사
            if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPwd())) {
                throw new IllegalArgumentException("전과 동일한 비밀번호는 사용할 수 없습니다.");
            }
            // 새 비밀번호 확인하기
            if (!isValidPassword(requestDto.getNewPassword())) {
                throw new IllegalArgumentException("새로운 비밀번호는 8자 이상이며 특수문자가 하나 이상 포함되어야 합니다.");
            }
            // 새 비밀번호 암호화 후 저장
            user.updatePwd(passwordEncoder.encode(requestDto.getNewPassword()));
        }
        User updateUser = userRepository.save(user);
        return UserResponseDto.fromEntity(updateUser);
    }

    public boolean isValidPassword(String password) {
        //비밀번호가 8자 미만이면 fail
        if (password == null || password.length() < 8) {
            return false;
        }
        //특수문자가 하나라도 있는지 체크하기
        return password.matches(".*[^a-zA-Z0-9].*");
    }

    //id가 중복인가 확인하기
    public boolean checkDuplicateLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
    //email이 중복인가 확인하기
    public boolean checkDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}