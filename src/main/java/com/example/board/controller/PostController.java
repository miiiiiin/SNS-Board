package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    // 로거 정의
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts(Authentication authentication) {
        logger.info("GET /api/v1/posts");
        var posts = postService.getPosts((UserEntity) authentication.getPrincipal());
        // 200 ok와 응답으로 내려줌
        return ResponseEntity.ok(posts);
    }

    /**
     * isLiking 상태값 파악하기 위해서는 단건 조회 API를 호출하는 유저의 정보가 필요하기 때문에 auth 파라미터 추가
     * @param postId
     * @param authentication
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId, Authentication authentication) {
        logger.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId, (UserEntity) authentication.getPrincipal());
//        return matchingPost
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(post);
    }

    // 사용자가 다음 api 호출할 때, jwt 인증 정보가 담긴 토큰을 함께 전달해줌
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody requestBody,
                                           Authentication authentication) {
        logger.info("POST /api/v1/posts");
        // authentication.getPrincipal() : 이때 가져온 사용자 정보는 userdetails => UserEntity로 변환하여 사용
        var post = postService.createPost(requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody requestBody, Authentication authentication) {
        logger.info("PATCH /api/v1/posts/{}", postId);
        var post = postService.updatePost(postId, requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        logger.info("DELETE /api/v1/posts/{}", postId);
        postService.deletePost(postId, (UserEntity) authentication.getPrincipal());
        // 204 : nocontent statuscode
        return ResponseEntity.noContent().build();
    }

    /**
     * LIKE 기능
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Post> toggleLike(@PathVariable Long postId, Authentication authentication) {
        var post = postService.toggleLike(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }
}
