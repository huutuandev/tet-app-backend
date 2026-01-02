package com.tet.tet_app.service;

import com.tet.tet_app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // Đăng ký hoặc lấy user từ DB
        User user = userService.registerOrLoginGoogle(googleId, email, name, picture);

        // Tạo authorities từ roles
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getName()))
        );

        // Trả về DefaultOAuth2User (implements OAuth2User + UserDetails)
        // Dùng "email" làm name attribute key để dễ extract sau
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),  // giữ nguyên tất cả attributes từ Google (sub, email, name, picture...)
                "email"                      // name attribute key → authentication.getName() sẽ trả email
        );
    }
}