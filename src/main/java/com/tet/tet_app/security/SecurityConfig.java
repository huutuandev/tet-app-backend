package com.tet.tet_app.security;

import com.tet.tet_app.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.tet.tet_app.security.jwt.JwtAuthenticationFilter;
import com.tet.tet_app.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép ĐĂNG KÝ và ĐĂNG NHẬP EMAIL mà KHÔNG bị redirect
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // Các endpoint auth khác cần JWT
                        .requestMatchers("/api/auth/**").authenticated()

                        // Google OAuth2 endpoints
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // Share link lời chúc
                        .requestMatchers("/api/wishes/share/**").permitAll()
                        .requestMatchers("/api/profile/**").hasRole("USER")
                        // Tất cả còn lại cần token
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google") // chỉ kích hoạt khi gọi trực tiếp
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}