package com.example.board.model.user;

import com.example.board.model.entity.UserEntity;

import java.time.ZonedDateTime;

// DTO (민감 및 불필요한 정보 제외, 따로 전달하는 객체)
public record Follower(
        Long userId,
        String username,
        String profile,
        String description,
        Long followersCount,
        Long followingsCount,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime followedDateTime,
        Boolean isFollowing
) {


    public static Follower from(User user, ZonedDateTime followedDateTime) {
        return new Follower(
                user.userId(),
                user.username() ,
                user.profile(),
                user.description(),
                user.followersCount(),
                user.followingsCount(),
                user.createdDateTime(),
                user.updatedDateTime(),
                followedDateTime,
                user.isFollowing()
        );
    }
}
