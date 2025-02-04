package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.*;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;

    /**
     * 유저 리스트 조회
     * 쿼리 파라미터로 검색
     * @param query
     * @return
     * 검색어가 없을 경우에는 모든 유저 반환, 그렇지 않을 경우, 검색어가 포함된 사용자 반환
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query) {
        var users = userService.getUsers(query);
        return ResponseEntity.ok(users);

    }


    /**
     * User 단건 조회
     * @return
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        var user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 정보 description 수정하는 api
     * @param username
     * @param requestBody
     * @param authentication
     * 사용자 인증정보 (수정하는 주체가 동일한 사용자인지 검증 위해)
     * @return
     */
    @PatchMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username,
                                           @RequestBody UserPatchRequestBody requestBody,
                                           Authentication authentication) {
        var user = userService.updateUser(username, requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    /**
     *
     * @param username
     * @return
     */
    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username) {
        var posts = postService.getPostsByUsername(username);
        return ResponseEntity.ok(posts);
    }


    /**
     * 회원가입, 로그인 API
     * @param requestBody
     * @return
     */

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
