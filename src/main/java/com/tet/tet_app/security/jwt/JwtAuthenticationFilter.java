package com.tet.tet_app.security.jwt;

import com.tet.tet_app.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException ex) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Token expired", "Phiên đăng nhập đã hết hạn");
            return;
        } catch (MalformedJwtException ex) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Token không hợp lệ");
            return;
        } catch (Exception ex) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Token không hợp lệ");
            return;
        }


        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response,
                            HttpStatus status,
                            String error,
                            String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        String body = """
        {
          "status": %d,
          "error": "%s",
          "message": "%s"
        }
        """.formatted(status.value(), error, message);

        response.getWriter().write(body);
    }

}
