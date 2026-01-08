package com.tet.tet_app.security.oauth;

import com.tet.tet_app.entity.User;
import com.tet.tet_app.security.jwt.JwtService;
import com.tet.tet_app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");
        String avatar = oAuth2User.getAttribute("picture");

        User user = userService.registerOrGetGoogleUser(
                googleId, email, name, avatar
        );

        String token = jwtService.generateTokenFromEmail(user.getEmail());

        // ✅ SET COOKIE ĐÚNG CÁCH (DEV)
        response.addHeader(
                "Set-Cookie",
                "access_token=" + token +
                        "; HttpOnly" +
                        "; Path=/" +
                        "; Max-Age=3600" +
                        "; SameSite=Lax"
        );

        response.sendRedirect("http://localhost:3000/oauth2/success");
    }
}

