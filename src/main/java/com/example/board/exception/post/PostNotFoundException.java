package com.example.board.exception.post;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

// 게시물이 발견되지 않았을 때 던져주는 예외
public class PostNotFoundException extends ClientErrorException {
    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "POST NOT FOUND");
    }

    // 예외가 발생했을 때, 구체적인 postid를 알고 있을 경우
    public PostNotFoundException(Long postId) {
        super(HttpStatus.NOT_FOUND, "Post with PostId " + postId + " NOT FOUND");
    }

    // postid는 모르지만 구체적인 메시지를 남기고 싶을 경우
    public PostNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
