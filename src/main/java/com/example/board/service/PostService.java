package com.example.board.service;

import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final List<Post> posts = new ArrayList<>();

    static {
        posts.add(new Post(1L, "POST 1", ZonedDateTime.now()));
        posts.add(new Post(2L, "POST 2", ZonedDateTime.now()));
        posts.add(new Post(3L, "POST 3", ZonedDateTime.now()));
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Optional<Post> getPostByPostId(Long postId) {
        // postiD() -> record 객체로 생성 시 사용 가능
        return posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
    }

    public Post createPost(PostPostRequestBody requestBody) {
        // 새롭게 저장될 포스트아이디
        var newPostId = posts.stream().mapToLong(Post::getPostId).max().orElse(0L) + 1;
        var newPost = new Post(newPostId, requestBody.body(), ZonedDateTime.now());
        // db에 저장하는 것 대체
        posts.add(newPost);
        return newPost;
    }

    public Post updatePost(Long postId, PostPatchRequestBody requestBody) {
        // 수정하고자 하는 대상 게시물 찾기
        Optional<Post> postOptional = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
        if (postOptional.isPresent()) {
            Post postToUpdate = postOptional.get();
            postToUpdate.setBody(requestBody.body());
            return postToUpdate;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    public void deletePost(Long postId) {
        Optional<Post> postOptional = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
        if (postOptional.isPresent()) {
            posts.remove(postOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }
}
