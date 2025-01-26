package com.example.board.controller;

import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<ArrayList<Post>> getPosts() {
        List<Post> posts = postService.getPosts();
        // 200 ok와 응답으로 내려줌
        return ResponseEntity.ok((ArrayList<Post>) posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        Optional<Post> matchingPost = postService.getPostByPostId(postId);
        return matchingPost
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody requestBody) {
        var post = postService.createPost(requestBody);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody requestBody) {
        var post = postService.updatePost(postId, requestBody);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void > deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        // 204 : nocontent statuscode
        return ResponseEntity.noContent().build();
    }
}
