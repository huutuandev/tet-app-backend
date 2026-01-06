package com.tet.tet_app.security;

import com.tet.tet_app.security.handler.CustomAccessDeniedHandler;
import com.tet.tet_app.security.handler.CustomAuthenticationEntryPoint;
import com.tet.tet_app.security.jwt.JwtAuthenticationFilter;
import com.tet.tet_app.security.oauth.CustomOAuth2UserService;
import com.tet.tet_app.security.oauth.OAuth2AuthenticationSuccessHandler;
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
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;
        private final CustomAccessDeniedHandler accessDeniedHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())

                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler)
                    )

                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/api/auth/register",
                                    "/api/auth/login",
                                    "/oauth2/**",
                                    "/login/oauth2/**",
                                    "/api/shop/items"
                            ).permitAll()

                            .requestMatchers(
                                    "/api/profile/**",
                                    "/api/house/me",
                                    "/api/shop/buy",
                                    "/api/shop/inventory",
                                    "/api/house/me",
                                    "/api/house/place",
                                    "/api/house/**"
                            ).hasRole("USER")

                            .anyRequest().authenticated()
                    )

                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/oauth2/authorization/google")
                            .userInfoEndpoint(userInfo ->
                                    userInfo.userService(customOAuth2UserService)
                            )
                            .successHandler(oAuth2SuccessHandler)
                    )

                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }

