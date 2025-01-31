package com.example.board.controller;

import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Post>> getPosts() {
        logger.info("GET /api/v1/posts");
        var posts = postService.getPosts();
        // 200 ok와 응답으로 내려줌
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        logger.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId);
//        return matchingPost
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody requestBody) {
        logger.info("POST /api/v1/posts");
        var post = postService.createPost(requestBody);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody requestBody) {
        logger.info("PATCH /api/v1/posts/{}", postId);
        var post = postService.updatePost(postId, requestBody);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void > deletePost(@PathVariable Long postId) {
        logger.info("DELETE /api/v1/posts/{}", postId);
        postService.deletePost(postId);
        // 204 : nocontent statuscode
        return ResponseEntity.noContent().build();
    }
}
