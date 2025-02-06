package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.reply.Reply;
import com.example.board.model.user.*;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
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
    @Autowired
    ReplyService replyService;

    /**
     * 현재 로그인된 상태 (좋아요, 팔로우 api 호출하는 주체)의 유저 정보가 필요하여 authentication 파라미터 추가
     * 유저 리스트 조회
     * 쿼리 파라미터로 검색
     * @param query
     * @return
     * 검색어가 없을 경우에는 모든 유저 반환, 그렇지 않을 경우, 검색어가 포함된 사용자 반환
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query, Authentication authentication) {
        var users = userService.getUsers(query, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(users);

    }


    /**
     * User 단건 조회
     * @return
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username, Authentication authentication) {
        var user = userService.getUser(username, (UserEntity) authentication.getPrincipal());
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
     * isLiking 상태값 체크하기 위해 authentication 파라미터 추가
     * @param username
     * @param authentication
     * @return
     */
    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username, Authentication authentication) {
        var posts = postService.getPostsByUsername(username, (UserEntity) authentication.getPrincipal());
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


    // TODO: FOLLOW 기능

    /**
     * username을 가진 유저를 팔로우 하겠다
     * @param username
     * @return
     */
    @PostMapping("/{username}/follows")
    public ResponseEntity<User> follow(@PathVariable String username, Authentication authentication) {
        // follow하고자 하는 대상, 현재 follow api를 호출하는 주체(follower가 될 사람)
        var user = userService.follow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    /**
     * follow 취소 기능
     * follow 주체는 api 호출하는 주체 본인이기 때문에 별도의 키값(followId)가 필요하지 않음
     * @param username
     * @param authentication
     * @return
     */
    @DeleteMapping("/{username}/follows")
    public ResponseEntity<User> unfollow(@PathVariable String username, Authentication authentication) {
        // follow하고자 하는 대상, 현재 follow api를 호출하는 주체(follower가 될 사람)
        var user = userService.unfollow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }


    /**
     * 팔로잉, 팔로워 목록 조회
     * authentication 필요x (현재 로그인된 유저에 대한 정보 확인할 때 필요한 것)
     */
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<Follower>> getFollowersByUser(@PathVariable String username, Authentication authentication) {
        var followers = userService.getFollowersByUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/followings")
    public ResponseEntity<List<User>> getFollowingsByUser(@PathVariable String username, Authentication authentication) {
        var followings = userService.getFollowingsByUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(followings);
    }


    @GetMapping("/{username}/replies")
    public ResponseEntity<List<Reply>> getRepliesByUser(@PathVariable String username) {
        // username 기준으로 찾아낸 답글들 반환
        var replies = replyService.getRepliesByUser(username);
        return ResponseEntity.ok(replies);
    }


    @GetMapping("/{username}/liked-users")
    public ResponseEntity<List<LikedUser>> getLikedUsersByUser(@PathVariable String username, Authentication authentication) {
        var likedUsers = userService.getLikedUsersByUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(likedUsers);
    }

}
