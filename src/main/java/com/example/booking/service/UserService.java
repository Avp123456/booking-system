package com.example.booking.service;

import com.example.booking.model.Role;
import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createIfNotExists(String username, String rawPassword, Set<Role> roles) {
        if (userRepository.existsByUsername(username)) {
            return userRepository.findByUsername(username).orElseThrow();
        }
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles(roles)
                .enabled(true)
                .build();
        return userRepository.save(u);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        var authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), u.isEnabled(),
                true, true, true, authorities);
    }
}
