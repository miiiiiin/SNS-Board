package com.example.board.model.reply;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

// DTO 역할
// 실제 사용하고자 하는 것들만 추림
// deletedDateTime은 null로 처리 됨. => 아무 의미 없음
// 아래는 null이 아닐 경우에만 json으로 변환시켜줌
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Reply(
        Long postId,
        String body,
        // 게시물을 작성한 유저 정보 (클라이언트로 내려줄 것이기 때문에 UserEntity가 아닌 User(DTO) 형식으로 보냄)
        User user,
        Post post,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime deletedDateTime
) {

    // ReplyEntity 받아서 Reply record 객체 형태로 변환
    public static Reply from(ReplyEntity replyEntity) {
        return new Reply(
                replyEntity.getReplyId(),
                replyEntity.getBody(),
                User.from(replyEntity.getUser()),
                Post.from(replyEntity.getPost()),
                replyEntity.getCreatedDateTime(),
                replyEntity.getUpdatedDateTime(),
                replyEntity.getDeletedDateTime());
    }