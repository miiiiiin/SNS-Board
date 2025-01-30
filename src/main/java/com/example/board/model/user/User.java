package com.example.board.model.user;

import com.example.board.model.entity.UserEntity;

import java.time.ZonedDateTime;

// DTO (민감 및 불필요한 정보 제외, 따로 전달하는 객체)
public record User(
        Long userId,
        String username,
        String profile,
        String description,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime
) {
    public static User from(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getProfile(),
                userEntity.getDescription(),
                userEntity.getCreatedDateTime(),
                userEntity.getUpdatedDateTime()
        );
    }
}
