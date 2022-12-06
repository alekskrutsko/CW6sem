package com.cw6sem.service;

import com.cw6sem.controller.AuthController;
import com.cw6sem.entity.User;
import com.cw6sem.repository.UserRepository;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    private UserDetailsService userDetailsService;

    public User findByEmailAndPassword(String email, String password){
        User user = userRepository.findByLoginAndBlocked(email, false);
        if(user == null) return null;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(authenticationToken);
        if(authenticationToken.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return user;
        }

        return null;
    }
    public User saveUser(User user){
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);
        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLogin());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());

        if(authenticationToken.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        return user;
    }
    public User findByLogin(String email){
        return userRepository.findByLogin(email);
    }
    public void userToggleBlocked(Long id){
        User user = userRepository.getById(id);
        boolean isBlocked = user.isBlocked();
        user.setBlocked(!isBlocked);
        userRepository.save(user);
    }

    public User updateUser(User newUser){
        User user = userRepository.findByLogin(AuthController.loggedUser.getLogin());
        if (!StringUtils.isNullOrEmpty(newUser.getLogin())) user.setLogin(newUser.getLogin());
        if (!StringUtils.isNullOrEmpty(newUser.getFirstName())) user.setFirstName(newUser.getFirstName());
        if (!StringUtils.isNullOrEmpty(newUser.getSurname())) user.setSurname(newUser.getSurname());
        if (!StringUtils.isNullOrEmpty(newUser.getPatronymic())) user.setPatronymic(newUser.getPatronymic());
        if (!StringUtils.isNullOrEmpty(newUser.getPassword())) {
            user.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(12)));
        }
        if (!StringUtils.isNullOrEmpty(newUser.getPhone())) user.setPhone(newUser.getPhone());

        return userRepository.save(user);
    }
}
