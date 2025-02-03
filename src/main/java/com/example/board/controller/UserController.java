package com.example.board.controller;

import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserLoginRequestBody;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    // @valid 어노테이션으로 유효성 검증 옵션 추가
    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody requestBody) {
        var user = userService.signUp(
                requestBody.username(),
                requestBody.password()
        );
        return ResponseEntity.ok(user);
//        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // 로그인
    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody requestBody) {
        // 해당 유저를 기반으로 생성한 jwt 인증 액세스 토큰 생성 후, 그 액세스 토큰을 담고 있는 별도의 리스폰스를 만들어서 클라이언트에 내려줌
        var response = userService.login(
                requestBody.username(),
                requestBody.password()
        );
        return ResponseEntity.ok(response);
//        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
