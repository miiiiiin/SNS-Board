package com.example.board.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // JwtExceptionFilter 이후에 동작해야하는 필터들이 계속해서 처리됨
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            // TODO: JWT 관련 커스텀 에러 메시지 생성하여 RESPONSE로 내려줌
            // 원하는 형태로 리스폰스 가공 가능 (json type)
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 코드로 내려줌
            response.setCharacterEncoding("UTF-8");

            // 디테일한 에러 정보
            var errorMap = new HashMap<String, Object>();
            errorMap.put("status", HttpStatus.UNAUTHORIZED);
            errorMap.put("message", e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            // json으로 시리얼라이즈
            String responseJson = objectMapper.writeValueAsString(errorMap);
            response.getWriter().write(responseJson);
        }
    }
}
