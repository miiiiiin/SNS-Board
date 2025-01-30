package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

// 사용자가 발견되지 않았을 때 던져주는 예외
public class UserNotFoundException extends ClientErrorException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "USER NOT FOUND");
    }

    // 예외가 발생했을 때, 구체적인 postid를 알고 있을 경우
    public UserNotFoundException(String username) {
        super(HttpStatus.NOT_FOUND, "USER with username " + username + " NOT FOUND");
    }
}