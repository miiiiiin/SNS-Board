package com.example.board.model.user;

import com.example.board.model.entity.UserEntity;

import java.time.ZonedDateTime;

// DTO (민감 및 불필요한 정보 제외, 따로 전달하는 객체)
public record LikedUser(
        Long userId,
        String username,
        String profile,
        String description,
        Long followersCount,
        Long followingsCount,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        Boolean isFollowing,
        Long likedPostId,
        ZonedDateTime likedDateTime
) {

    /**
     * 새로운 필드가 추가됨에 따라 기존 from 메서드를 호출하는 곳에서 에러가 발생 가능
     * 점진적으로 수정하기 위해 isFollowing null로 초기화
     * @param
     * @return
     */
    public static LikedUser from(User user, Long likedPostId, ZonedDateTime likedDateTime) {
        return new LikedUser(
                user.userId(),
                user.username(),
                user.profile(),
                user.description(),
                user.followersCount(),
                user.followingsCount(),
                user.createdDateTime(),
                user.updatedDateTime(),
                user.isFollowing(),
                likedPostId,
                likedDateTime
        );
    }
}
