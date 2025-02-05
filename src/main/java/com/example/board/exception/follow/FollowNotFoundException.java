package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import com.example.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends ClientErrorException {

    public FollowNotFoundException() {
        super(HttpStatus.NOT_FOUND, "FOLLOW NOT FOUND");
    }

    // id는 모르지만 구체적인 메시지를 남기고 싶을 경우
    public FollowNotFoundException(UserEntity follower, UserEntity following) {
        super(HttpStatus.NOT_FOUND, "Follow with follower " + follower.getUsername() + " and follower" +
                following.getUsername() + "NOT FOUND");
    }
}
