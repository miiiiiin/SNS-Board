package com.example.board.controller;

import com.example.board.model.user.User;
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
}
