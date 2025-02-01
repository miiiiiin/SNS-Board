package com.example.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfiguration {

    @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired private JwtExceptionFilter jwtExceptionFilter;

    // cors 관련 설정 커스터마이징
    @Bean
    public CorsConfigurationSource corsConfiguration() {
        // cors 설정 틀
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용하고자 하는 오리진의 목록 설정 (프론트 포트 3000)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        // api가 호출될 때, 어떤 http method를 허용할지 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
        // 헤더 모든 값 허용하도록 설정
        configuration.setAllowedHeaders(List.of("*"));
        // 인증 정보 포함 허용 (쿠키 등) : 클라이언트에서 인증 정보(쿠키 등)를 포함하여 요청하는 경우, CORS 정책에서 이를 허용해야 함
        configuration.setAllowCredentials(true);
        // 어떤 api 패턴에 대해 적용할 지 설정 (특정 api 패턴에 대해 제한적으로 적용 가능)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }

    // SpringBootWebSecurityConfiguration 커스터마이징
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //  cors 설정 활성화
        http
//                .cors(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .authorizeHttpRequests((requests) ->
                        // 프리플라이트 요청 허용
                        // Spring Security는 기본적으로 모든 요청을 차단하므로 OPTIONS 메서드로 프리플라이트 요청을 허용해야 함
                        requests
                                .requestMatchers(HttpMethod.POST,"/api/*/users")
                                .permitAll() // users ..로 시작하는 모든 api는 모두 허용
                                .anyRequest()
                                .authenticated() // 나머지 모든 api는 반드시 인증을 해야 함 (회원가입 로그인 제외, 사용자 인증 사용할 것)

//                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                                .anyRequest().authenticated()
                ) // 모든 request에 대해서 인증 처리
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 사용자 인증과 관련된 session : RESTAPI는 무상태이기에 세션 정보가 필요하지 않음 (STATELESS: 세션이 생성되지 않도록 함)
                .csrf(CsrfConfigurer::disable)  // CSRF 검증 비활성화 (REST API의 경우 사용하지 않음)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // jwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter.class 바로 앞에 추가해줌
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // basic auth 사
        return http.build();
    }
}
