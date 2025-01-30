package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "\"user\"") // postgresql "user" 예약어 방지
@SQLDelete(sql = "UPDATE \"user\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE userid = ?") // soft delete
@SQLRestriction("deleteddatetime IS NULL")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String username;

    @Column
    private String password;

    // 프로필 사진 이미지 주소
    @Column
    private String profile;

    // 자기소개
    @Column
    private String description;

    @Column
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    // admin, user 안나눌 거기 때문에 그대로 둠
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }

    public static UserEntity of (String username, String password) {
        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);

        // 각 유저 생성 시 랜덤 프로필 placeholder 이미지 사진 설정 (1~100)
        userEntity.setProfile("https://avatar.iran.liara.run/public/" + new Random().nextInt(100));

        return userEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(profile, that.profile) && Objects.equals(description, that.description) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(updatedDateTime, that.updatedDateTime) && Objects.equals(deletedDateTime, that.deletedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, profile, description, createdDateTime, updatedDateTime, deletedDateTime);
    }

    // PRE 어노테이션 : jpa에 의해서 실제 데이터가 내부적으로 저장,업데이트 되기 직전에 원하는 로직을 할 수 있음
    @PrePersist
    private void prePersist() {
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
