package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

// JPA 적용
@Entity
@Table(name = "post",
indexes = @Index(name = "post_userid_idx", columnList = "userid")) // "userid" 컬럼에 대한 인덱스
// JPA를 통해 삭제 처리 될 때, 내부적으로 DELETE 대신 아래 sql이 실행되 SOFT DELETE 처리 됨
// 디비 내에서 완전히 삭제하는 것이 아니라, deletedDatetime을 현재 시간으로 업데이트 해주는 방식으로 처리함
@SQLDelete(sql = "UPDATE \"post\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE postid = ?")
// 실제 서비스에서 보여주기 위해 조회 용도로 게시물 꺼낼 때에는 (삭제된 적이 없는) 게시물만 보여줌
@SQLRestriction("deleteddatetime IS NULL")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @Column(columnDefinition = "TEXT")
    private String body;

    // 댓글 집계
    @Column
    private Long repliesCount = 0L;

    @Column
    private ZonedDateTime createdDateTime;
    @Column
    private ZonedDateTime updatedDateTime;
    @Column
    private ZonedDateTime deletedDateTime;


    /**
     * 게시물과 유저의 관계는 N:1
     * JOIN 컬럼으로 UserEntity의 프라이머리 키 값 "userid"로 설정
     * 실제 테이블에서는 "userid"라는 컬럼으로 추가됨. 실제 데이터 관점에서 db에 저장될 때에는
     * "userid"기반으로 연동되나 실제 코드 작성 시에는 user만 사용해도 내부적으로 userid만으로 user를 가져와서 세팅 가능
     */
    @ManyToOne
    @JoinColumn(name = "userid")
    private UserEntity user;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Long repliesCount) {
        this.repliesCount = repliesCount;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public ZonedDateTime getDeletedDateTime() {
        return deletedDateTime;
    }

    public void setDeletedDateTime(ZonedDateTime deletedDateTime) {
        this.deletedDateTime = deletedDateTime;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public static PostEntity of (String body, UserEntity user) {
        var post = new PostEntity();
        post.setBody(body);
        // postEntity에 현재 로그인된 유저정보로 user 세팅
        post.setUser(user);
        return post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity post = (PostEntity) o;
        return Objects.equals(postId, post.postId) && Objects.equals(body, post.body) && Objects.equals(repliesCount, post.repliesCount) && Objects.equals(createdDateTime, post.createdDateTime) && Objects.equals(updatedDateTime, post.updatedDateTime) && Objects.equals(deletedDateTime, post.deletedDateTime) && Objects.equals(user, post.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, body, repliesCount, createdDateTime, updatedDateTime, deletedDateTime, user);
    }

    // PRE 어노테이션 : jpa에 의해서 실제 데이터가 내부적으로 저장,업데이트 되기 직전에 원하는 로직을 할 수 있음
    @PrePersist
    private void prePersist() {
        // postentity 처음 생성될 때, 현재 시간 기록. 생성했을 때에도
        // 수정 시간을 생성 시간과 동일하게 맞춰줌
         this.createdDateTime = ZonedDateTime.now();
         this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    private void preUpdate() {
        // 업데이트가 발생된 시점을 현재 시점으로 기록
        this.updatedDateTime = ZonedDateTime.now();
    }
}
