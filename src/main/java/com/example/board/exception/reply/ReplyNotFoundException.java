package com.example.board.exception.reply;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ClientErrorException {

    public ReplyNotFoundException() {
        super(HttpStatus.NOT_FOUND, "REPLY NOT FOUND");
    }

    // 예외가 발생했을 때, 구체적인 postid를 알고 있을 경우
    public ReplyNotFoundException(Long replyId ) {
        super(HttpStatus.NOT_FOUND, "REPLY with ReplyId " + replyId + " NOT FOUND");
    }

    // postid는 모르지만 구체적인 메시지를 남기고 싶을 경우
    public ReplyNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
