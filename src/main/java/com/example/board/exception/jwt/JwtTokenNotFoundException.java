package com.example.board.exception.jwt;

import com.example.board.config.JwtExceptionFilter;
import io.jsonwebtoken.JwtException;

public class JwtTokenNotFoundException extends JwtException {
    public JwtTokenNotFoundException() {
        super("JWT NOT FOUND");
    }
}
