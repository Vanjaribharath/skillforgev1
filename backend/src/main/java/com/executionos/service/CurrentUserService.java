package com.executionos.service;

import com.executionos.model.User;
import com.executionos.repository.UserRepository;
import java.security.Principal;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository users;

    public CurrentUserService(UserRepository users) {
        this.users = users;
    }

    public User requireUser(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Authentication is required");
        }
        return users.findByEmail(principal.getName()).orElseThrow();
    }
}
