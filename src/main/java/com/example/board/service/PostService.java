package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.PostEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {

    @Autowired private PostEntityRepository postEntityRepository;

    public List<Post> getPosts() {
        // 이대로 넘겨주면 raw data를 통으로 넘겨주기 때문에, 서비스에 필요한 필드들만 선별하여 별도의 dto를 구성하여 넉며줌
        // 내부적으로 적절한 sql로 변환되어 실행됨
        var postEntities = postEntityRepository.findAll();
        // postEntity 리스트 요소 POST 형태로 변환해서 리턴
        return postEntities.stream().map(Post::from).toList();
    }

    public Post  getPostByPostId(Long postId) {
        // postId() -> record 객체로 생성 시 사용 가능
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));
        // optional 형태인데 데이터가 존재하지 않는다면 예외처리 던짐
        // POST DTO로 변환해서 반환
        return Post.from(postEntity);
    }

    public Post createPost(PostPostRequestBody requestBody) {
        var postEntity = new PostEntity();
        postEntity.setBody(requestBody.body());
        // 리포지토리에 실제 저장 수행
        // 실제 저장되어 있는 데이터 값을 변수로 받음
        var savedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody requestBody) {
        // 수정하고자 하는 대상 게시물 찾기

        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));
        // 수정할 데이터 넘겨주기
        postEntity.setBody(requestBody.body());
        var updatedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "POST NOT FOUND"));

        postEntityRepository.delete(postEntity);
    }
}
