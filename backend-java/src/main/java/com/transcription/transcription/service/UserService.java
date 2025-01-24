package com.transcription.transcription.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transcription.transcription.model.User;
import com.transcription.transcription.repository.UserRepository;

@Service
public class UserService {

	@Autowired
    private UserRepository userRepository;

    public  User findByUsername(String username, String password) {
        // Fetch user from DB by username
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new RuntimeException("User not found");
}
    

    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }
        User user = new User(username, password);
        return userRepository.save(user);
    }
}

