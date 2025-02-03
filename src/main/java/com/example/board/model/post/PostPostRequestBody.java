package com.example.board.model.post;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Objects;


public record PostPostRequestBody(String body) {}

// Post 리소스를 위한 POST 방식의 리퀘스트 바디
//public class PostPostRequestBody {
//    private String body;
//
//    public PostPostRequestBody(String body) {
//        this.body = body;
//    }
//
//    public PostPostRequestBody() {
//        // @RequestBody 어노테이션 정상 동작위해 파라미터가 빈 생성자가 필요
//    }
//
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        PostPostRequestBody that = (PostPostRequestBody) o;
//        return Objects.equals(body, that.body);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(body);
//    }
//}
