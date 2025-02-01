package com.example.board.config;

import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: jwt 토큰 검증 로직
        String BEARER_PREFIX = "Bearer ";
        var auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 인증 완료되면 securityContext에 인증정보 설정
        var securityContext = SecurityContextHolder.getContext();

        // 통신시 header에서 토큰 제대로 전달받지 않은 경우
//        if (ObjectUtils.isEmpty(auth) || auth.startsWith(BEARER_PREFIX)) {
//            throw new JwtTokenNotFoundException();
//        }

        /**
         * AUTH이 비어있지 않고, auth이 bearer prefix로 시작하는 경우에 한해 jwt token 인증
         * SecurityContext의 auth가 비어있는 경우도 포함
         */
        // auth가 BEARER_PREFIX로 시작하지 않으면 jwt 검증 자체를 하지 않음
        if (!ObjectUtils.isEmpty(auth) && auth.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null) {
            // accessToken값 BEARER_PREFIX 크기만큼 추출
            var accessToken = auth.substring(BEARER_PREFIX.length());
            // accesstoken으로 jwtservice에서 유저네임 추출
            var username = jwtService.getUsername(accessToken);
            // 유저네임으로 userService에서 UserDetails 추출
            var userDetails = userService.loadUserByUsername(username);
            // 인증 종료되면 (해당 인증된 사용자 정보를 securityContext에 설정해놓아야 이후에 controller 단에서 해당 인증정보 사용 가능)
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            // 현재 처리되고 있는 HTTP Request 정보 설정
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 인증정보 세팅
            securityContext.setAuthentication(authenticationToken);
            // SecurityContextHolder 통해서 해당 securityContext를 저장
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
