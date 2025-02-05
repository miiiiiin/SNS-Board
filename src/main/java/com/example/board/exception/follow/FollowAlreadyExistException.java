package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import com.example.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowAlreadyExistException extends ClientErrorException {
    public FollowAlreadyExistException() {
        super(HttpStatus.CONFLICT, "FOLLOW ALREADY EXISTS");
    }

    // 예외가 발생했을 때, 구체적인 postid를 알고 있을 경우
    public FollowAlreadyExistException(UserEntity follower, UserEntity following) {
        super(HttpStatus.CONFLICT, "Follow with follower " + follower.getUsername() + " and follower" +
                following.getUsername() + "already exists");
    }
}