package com.cw6sem.security;

import com.cw6sem.domain.Role;
import com.cw6sem.entity.User;
import com.cw6sem.repository.UserRepository;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(email);
        return getUserDetails(user);
    }

    public static UserDetails getUserDetails(User user) {
        System.out.println(user.getRole().getAuthorities());
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(),
                true,
                true,
                true,
                true,
                user.getRole().getAuthorities()
        );
    }
}
