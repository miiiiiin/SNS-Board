package com.example.board.model;

import com.example.board.model.entity.PostEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;
import java.util.Objects;


// DTO 역할
// 실제 사용하고자 하는 것들만 추림
// deletedDateTime은 null로 처리 됨. => 아무 의미 없음
// 아래는 null이 아닐 경우에만 json으로 변환시켜줌
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
        Long postId,
        String body,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime deletedDateTime
) {

    // PostEntity 받아서 POST record 객체 형태로 변환
    public static Post from(PostEntity postEntity) {
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                postEntity.getCreatedDateTime(),
                postEntity.getUpdatedDateTime(),
                postEntity.getDeletedDateTime());
    }
}

//public class Post {
//    private Long postId;
//    private String body;
//    private ZonedDateTime createdDateTime;
//
//    public Post(Long postId, String body, ZonedDateTime createdDateTime) {
//        this.postId = postId;
//        this.body = body;
//        this.createdDateTime = createdDateTime;
//    }
//
//    public Long getPostId() {
//        return postId;
//    }
//
//    public void setPostId(Long postId) {
//        this.postId = postId;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    public ZonedDateTime getCreatedDateTime() {
//        return createdDateTime;
//    }
//
//    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
//        this.createdDateTime = createdDateTime;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Post post = (Post) o;
//        return Objects.equals(postId, post.postId) && Objects.equals(body, post.body) && Objects.equals(createdDateTime, post.createdDateTime);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(postId, body, createdDateTime);
//    }
//
//    @Override
//    public String toString() {
//        return "Post{" +
//                "postId=" + postId +
//                ", body='" + body + '\'' +
//                ", createdDateTime=" + createdDateTime +
//                '}';
//    }
//}
