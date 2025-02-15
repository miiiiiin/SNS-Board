package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.LikeEntityRepository;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired private PostEntityRepository postEntityRepository;
    @Autowired private UserEntityRepository userEntityRepository;
    @Autowired private LikeEntityRepository likeEntityRepository;

    public List<Post> getPosts(UserEntity currentUser) {
        // 이대로 넘겨주면 raw data를 통으로 넘겨주기 때문에, 서비스에 필요한 필드들만 선별하여 별도의 dto를 구성하여 넉며줌
        // 내부적으로 적절한 sql로 변환되어 실행됨
        var postEntities = postEntityRepository.findAll();
        // postEntity 리스트 요소 POST 형태로 변환해서 리턴
        return postEntities.stream().map(
                postEntity -> getPostWithLikingStatus(postEntity, currentUser))
                .toList();
    }

    public Post getPostByPostId(Long postId, UserEntity currentUser) {
        // postId() -> record 객체로 생성 시 사용 가능
        // optional 형태인데 데이터가 존재하지 않는다면 예외처리 던짐
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));

        // 현재의 currentUser가 postEntity를 좋아하고 있는지를 확인
//        var isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity)
//                .isPresent();
//        // POST DTO로 변환해서 반환
//        return Post.from(postEntity, isLiking);
        return getPostWithLikingStatus(postEntity, currentUser);
    }

    /**
     * isLiking 체크해서 POST 레코드로 반환해주는 함수(공통)
     * @param postEntity
     * @param currentUser
     * @return
     */
    private Post getPostWithLikingStatus(PostEntity postEntity, UserEntity currentUser) {
        // 현재의 currentUser가 postEntity를 좋아하고 있는지를 확인
        var isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity)
                .isPresent();
        // POST DTO로 변환해서 반환
        return Post.from(postEntity, isLiking);
    }

    // api를 호출한 현재의 유저를 currentUser로 설정
    public Post createPost(PostPostRequestBody requestBody, UserEntity currentUser) {
        var postEntity = PostEntity.of(requestBody.body(), currentUser);
        // 리포지토리에 실제 저장 수행
        // 실제 저장되어 있는 데이터 값을 변수로 받음
        var savedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody requestBody, UserEntity currentUser) {
        // 수정하고자 하는 대상 게시물 찾은 다음, 해당 게시물의 작성자와 현재 유저가 같은지를 검증

        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));

        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        // 수정할 데이터 넘겨주기
        postEntity.setBody(requestBody.body());
        var updatedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "POST NOT FOUND"));

        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        postEntityRepository.delete(postEntity);
    }

    public List<Post> getPostsByUsername(String username, UserEntity currentUser) {
        var userEntity = userEntityRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        var postEntities = postEntityRepository.findByUser(userEntity);
        return postEntities.stream().map(
                postEntity -> getPostWithLikingStatus(postEntity, currentUser))
                .toList();
    }

    /**
     * LIKE 기능
     * @param postId
     * @return
     */
    @Transactional
    public Post toggleLike(Long postId, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "POST NOT FOUND"));
        var likeEntity = likeEntityRepository.findByUserAndPost(currentUser, postEntity);

        if (likeEntity.isPresent()) {
            likeEntityRepository.delete(likeEntity.get());
            postEntity.setLikesCount(Math.max(0, postEntity.getLikesCount() - 1));
            return Post.from(postEntityRepository.save(postEntity), false);
        } else {
            likeEntityRepository.save(LikeEntity.of(currentUser, postEntity));
            postEntity.setLikesCount(postEntity.getLikesCount() + 1);
            return Post.from(postEntityRepository.save(postEntity), true);
        }
//        return Post.from(postEntityRepository.save(postEntity));
    }
}
