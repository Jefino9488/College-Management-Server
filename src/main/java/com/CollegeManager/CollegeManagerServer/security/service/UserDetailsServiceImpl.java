package com.CollegeManager.CollegeManagerServer.security.service;

import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ADDED
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAuthenticationRepository userAuthenticationRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        return userAuthenticationRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}