package com.example.board.exception;

import com.example.board.model.error.ClientErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// rest api 컨트롤러 전역에서 발생하는 예외 핸들링
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
        return new ResponseEntity<>(
                new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());

    }

    // MethodArgumentNotValidException를 처리할 수 있게ExceptionHandler에 전달
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(MethodArgumentNotValidException e) {
        // 클라이언트에게 에러 정보 추려서 보내주기 위해
        var errMessage = e.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList()
                .toString();
        return new ResponseEntity<>(
                // HttpStatus.BAD_REQUEST : client가 잘못된 리퀘스트 요청
                new ClientErrorResponse(HttpStatus.BAD_REQUEST, errMessage), HttpStatus.BAD_REQUEST);

    }

    // HttpMessageNotReadableException (body 없는 경우) 처리할 수 있게ExceptionHandler에 전달
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ClientErrorResponse> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                // HttpStatus.BAD_REQUEST : client가 잘못된 리퀘스트 요청
                new ClientErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);

    }

    /**
     * 서버 내부 발생 에러 핸들링
     * 내부 에러 전달하는 대신 스테이터스 코드만 보내주는 걸로 간소화
     * @param
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(RuntimeException e) {
        return ResponseEntity.internalServerError().build();

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
