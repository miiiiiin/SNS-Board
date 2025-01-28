package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebConfiguration {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 모든 request에 대해서 인증 처리
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 사용자 인증과 관련된 session : RESTAPI는 무상태이기에 세션 정보가 필요하지 않음 (STATELESS: 세션이 생성되지 않도록 함)
                .csrf(CsrfConfigurer::disable)  // csrf 검증은 제외
                .httpBasic(Customizer.withDefaults()); // basic auth 사
        return http.build();
    }
}
