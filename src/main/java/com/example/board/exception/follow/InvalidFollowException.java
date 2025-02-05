package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class InvalidFollowException extends ClientErrorException {

    public InvalidFollowException() {
        super(HttpStatus.BAD_REQUEST, "INVALID FOLLOW REQUEST");
    }


    // followid는 모르지만 구체적인 메시지를 남기고 싶을 경우
    public InvalidFollowException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
