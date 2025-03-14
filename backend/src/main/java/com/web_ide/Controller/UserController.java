package com.web_ide.Controller;

import com.web_ide.dto.UserRequestDto;
import com.web_ide.dto.UserResponseDto;
import com.web_ide.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto){
        UserResponseDto userResponseDto = userService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    //회원 정보 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId,
                                                      @Valid @RequestBody UserRequestDto userRequestDto){
        UserResponseDto updateUser = userService.updateUser(userId, userRequestDto);
        return ResponseEntity.ok(updateUser);
    }

    //회원 정보 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
