package com.shuhail.blogdemo.service;

import com.shuhail.blogdemo.domain.User;
import com.shuhail.blogdemo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
