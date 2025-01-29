package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

// JPA 적용
@Entity
@Table(name = "post")
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
    @Column
    private ZonedDateTime createdDateTime;
    @Column
    private ZonedDateTime updatedDateTime;
    @Column
    private ZonedDateTime deletedDateTime;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(postId, that.postId) && Objects.equals(body, that.body) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(updatedDateTime, that.updatedDateTime) && Objects.equals(deletedDateTime, that.deletedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, body, createdDateTime, updatedDateTime, deletedDateTime);
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
