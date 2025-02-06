package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.reply.ReplyNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.ReplyEntityRepository;
import com.example.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReplyService {

    @Autowired
    private  UserEntityRepository userEntityRepository;
    @Autowired private PostEntityRepository postEntityRepository;
    @Autowired private ReplyEntityRepository replyEntityRepository;

    public List<Reply> getRepliesByPostId(Long postId) {
        // postid 기준으로 게시물 단건 찾기
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));
        var replyEntities = replyEntityRepository.findByPost(postEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }

    // 댓글 생성 및 댓글 개수 증가 로직이 db 상에서 처리될 때, 하나의 transaction으로 묶여서 수행하게 됨. (데이터 정합성 보장 목적)
    @Transactional
    // api를 호출한 현재의 유저를 currentUser로 설정
    public Reply createReply(Long postId, ReplyPostRequestBody requestBody, UserEntity currentUser) {
        // postid 기준으로 postEntity(게시물) 찾아오기
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));

        var replyEntity = replyEntityRepository.save(ReplyEntity.of(requestBody.body(), currentUser, postEntity));
        // 댓글 개수 추가
        postEntity.setRepliesCount(postEntity.getRepliesCount() + 1);
        return Reply.from(replyEntity);
    }


    public Reply updateReply(Long postId, Long replyId, ReplyPatchRequestBody requestBody, UserEntity currentUser) {
        // 수정하고자 하는 대상 게시물 찾은 다음, 해당 게시물의 작성자와 현재 유저가 같은지를 검증
        // 실제 로직에 쓰이지는 않으나 게시물이 존재하는지 검증하는 용도로 씀
        postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));

        var replyEntity = replyEntityRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        // 수정할 body 내용 덮어쓰기
        replyEntity.setBody(requestBody.body());
        return Reply.from(replyEntityRepository.save(replyEntity));
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId));

        var replyEntity = replyEntityRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        replyEntityRepository.delete(replyEntity);

        // 게시물 댓글 수 감소 로직 (음수 값 방지)
        postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));
        postEntityRepository.save(postEntity);
    }

    /**
     * user가 쓴 답글 목록 가져오기
     * @param username
     * @return
     */
    public List<Reply> getRepliesByUser(String username) {
        // username 기준 찾아낸 userEntity
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        var replyEntities = replyEntityRepository.findByUser(userEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }
}
