package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

// JPA 적용
@Entity
@Table(name = "\"like\"",
indexes = {@Index(name = "like_userid_postid_idx", columnList = "userid, postid", unique = true)}) // unique true로 설정해서 중복 생성 방지

public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;
    @Column
    private ZonedDateTime createdDateTime;

    /**
     * 좋아요와 유저의 관계는 N:1
     * JOIN 컬럼으로 UserEntity의 프라이머리 키 값 "userid"로 설정
     * 실제 테이블에서는 "userid"라는 컬럼으로 추가됨. 실제 데이터 관점에서 db에 저장될 때에는
     * "userid"기반으로 연동되나 실제 코드 작성 시에는 user만 사용해도 내부적으로 userid만으로 user를 가져와서 세팅 가능
     */
    @ManyToOne
    @JoinColumn(name = "userid")
    private UserEntity user;

    /**
     * 게시물과 좋아요 관계 설정
     * (1:N)
     */
    @ManyToOne
    @JoinColumn(name = "postid")
    private PostEntity post;

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public static LikeEntity of(UserEntity user, PostEntity post) {
        var like = new LikeEntity();
        like.setPost(post);
        like.setUser(user);
        return like;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeEntity that = (LikeEntity) o;
        return Objects.equals(likeId, that.likeId) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(user, that.user) && Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeId, createdDateTime, user, post);
    }

    // PRE 어노테이션 : jpa에 의해서 실제 데이터가 내부적으로 저장,업데이트 되기 직전에 원하는 로직을 할 수 있음
    @PrePersist
    private void prePersist() {
        // likeEntity 처음 생성될 때, 현재 시간 기록. 생성했을 때에도
        // 수정 시간을 생성 시간과 동일하게 맞춰줌
         this.createdDateTime = ZonedDateTime.now();
    }
}
