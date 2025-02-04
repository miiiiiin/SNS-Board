package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postid}/replies")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    /**
     * 특정 게시물 기준으로 해당 게시물에 달린 모든 댓글 목록을 가져옴
     */
    @GetMapping
    public ResponseEntity<List<Reply>> getRepliesByPostId(@PathVariable Long postId) {
        var replies = replyService.getRepliesByPostId(postId);
        // 200 ok와 응답으로 내려줌
        return ResponseEntity.ok(replies);
    }

    /**
     * 댓글 생성 api
     * @param postId
     * @param requestBody
     * @param authentication
     * @return
     */
    @PostMapping()
    public ResponseEntity<Reply> createReply(@PathVariable Long postId,
                                             @RequestBody ReplyPostRequestBody requestBody,
                                             Authentication authentication) {
        // 맨 끝 매개변수 : 댓글 생성하기 위해 api 호출한 현재 로그인된 유저
        var reply =  replyService.createReply(postId, requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long postId,
                                             @PathVariable Long replyId,
                                             @RequestBody ReplyPatchRequestBody requestBody, Authentication authentication) {
        var reply = replyService.updateReply(postId, replyId, requestBody, (UserEntity) authentication.getPrincipal());

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void > deletePost(@PathVariable Long postId,
                                            @PathVariable Long replyId,
                                            Authentication authentication) {
        replyService.deleteReply(postId, replyId, (UserEntity) authentication.getPrincipal());
        // 204 : nocontent statuscode
        return ResponseEntity.noContent().build();
    }
}
