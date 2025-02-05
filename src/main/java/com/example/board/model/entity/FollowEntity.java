package com.example.board.model.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;

// JPA 적용
@Entity
@Table(name = "\"follow\"",
indexes = {@Index(name = "follow_follower_following_idx", columnList = "follower, following", unique = true)}) // unique true로 설정해서 중복 생성 방지

public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;
    @Column
    private ZonedDateTime createdDateTime;


    /**
     * 같은 userentity 내에서 follower와 following을 할 수 있음
     * 유저 : follower (여러 개 팔로우 가능)
     * 유저 : following (여러 개 팔로우 가능)
     * 1:N의 관계
     * 팔로우는 (팔로워, 팔로잉)각각 1명의 유저에게 종속
     *
     *
     *
     */
    @ManyToOne
    @JoinColumn(name = "follower")
    private UserEntity follower;

    @ManyToOne
    @JoinColumn(name = "following")
    private UserEntity following;

    /**
     * 게시물과 좋아요 관계 설정
     * (1:N)
     */
    @ManyToOne
    @JoinColumn(name = "postid")
    private PostEntity post;

    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public UserEntity getFollower() {
        return follower;
    }

    public void setFollower(UserEntity follower) {
        this.follower = follower;
    }

    public UserEntity getFollowing() {
        return following;
    }

    public void setFollowing(UserEntity following) {
        this.following = following;
    }

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public static FollowEntity of(UserEntity follower, UserEntity following) {
        var follow = new FollowEntity();
        follow.setFollower(follower);
        follow.setFollowing(following);
        return follow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowEntity that = (FollowEntity) o;
        return Objects.equals(followId, that.followId) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(follower, that.follower) && Objects.equals(following, that.following) && Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followId, createdDateTime, follower, following, post);
    }

    // PRE 어노테이션 : jpa에 의해서 실제 데이터가 내부적으로 저장,업데이트 되기 직전에 원하는 로직을 할 수 있음
    @PrePersist
    private void prePersist() {
        // likeEntity 처음 생성될 때, 현재 시간 기록. 생성했을 때에도
        // 수정 시간을 생성 시간과 동일하게 맞춰줌
         this.createdDateTime = ZonedDateTime.now();
    }
}
