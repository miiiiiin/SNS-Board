package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends ClientErrorException {
    public UserAlreadyExistException() {
        super(HttpStatus.CONFLICT, "USER ALREADY EXISTS");
    }

    // 예외가 발생했을 때, 구체적인 postid를 알고 있을 경우
    public UserAlreadyExistException(String username) {
        super(HttpStatus.CONFLICT, "USER with username " + username + " ALREADY EXISTS");
    }
}