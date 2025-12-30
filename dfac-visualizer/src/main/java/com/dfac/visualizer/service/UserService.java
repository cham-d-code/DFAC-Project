package com.dfac.visualizer.service;

import com.dfac.visualizer.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;

        // Create default demo users
        register("user1", "password1");
        register("user2", "password2");
    }

    public User register(String username, String password) {
        if (usersByUsername.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);

        usersByUsername.put(username, user);
        usersById.put(user.getId(), user);

        return user;
    }

    public Optional<User> authenticate(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }
}
