package com.tet.tet_app.security.jwt;

import com.tet.tet_app.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        // Bỏ qua các endpoint public
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy access token từ Authorization header
        String token = resolveAccessToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract email từ token
            String email = jwtService.extractEmail(token);

            // Nếu chưa authenticate và token hợp lệ
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Kiểm tra token chưa hết hạn
                if (!jwtService.isTokenExpired(token)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT authentication thành công cho user: {}", email);
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("JWT token đã hết hạn: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token đã hết hạn\", \"code\": \"TOKEN_EXPIRED\"}");
            return;

        } catch (JwtException e) {
            log.error("JWT token không hợp lệ: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token không hợp lệ\", \"code\": \"INVALID_TOKEN\"}");
            return;

        } catch (Exception e) {
            log.error("Lỗi xử lý JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Lấy access token từ Authorization header (Bearer token)
     * Không lấy từ cookie nữa - chỉ dùng header
     */
    private String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    /**
     * Kiểm tra endpoint có phải public không
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/verify-email") ||
                path.startsWith("/api/auth/resend-code") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/login/oauth2") ||
                path.startsWith("/api/uploads")||
                path.startsWith("/api/images");
    }
}