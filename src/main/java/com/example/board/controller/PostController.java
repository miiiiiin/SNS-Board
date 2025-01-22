package com.example.board.controller;

import com.example.board.model.Post;
import com.example.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/api/v1/posts")
    public ResponseEntity<List<Post>> getPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "POST 1", ZonedDateTime.now()));
        posts.add(new Post(2L, "POST 2", ZonedDateTime.now()));
        posts.add(new Post(3L, "POST 3", ZonedDateTime.now()));
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/api/v1/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {

        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "POST 1", ZonedDateTime.now()));
        posts.add(new Post(2L, "POST 2", ZonedDateTime.now()));
        posts.add(new Post(3L, "POST 3", ZonedDateTime.now()));

        Optional<Post> matchingPost = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();

        return matchingPost
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
