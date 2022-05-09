package com.cw6sem.service;

import com.cw6sem.controller.AuthController;
import com.cw6sem.entity.User;
import com.cw6sem.repository.UserRepository;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmailAndPassword(String email, String password){
        User user = userRepository.findByLoginAndBlocked(email, false);
        if(user == null) return null;
        if (BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
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
