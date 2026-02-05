package com.tet.tet_app.security.user;

import com.tet.tet_app.entity.User;
import com.tet.tet_app.repository.UserRepository;
//import com.tet.tet_app.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + email));

        return new CustomUserDetails(user); // Trả về CustomUserDetails chứa đầy đủ thông tin
    }
}