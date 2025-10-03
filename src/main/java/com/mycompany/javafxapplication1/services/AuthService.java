package com.mycompany.javafxapplication1.services;

import com.mycompany.javafxapplication1.models.User;

public class AuthService {
    public boolean authenticate(User user, String password) {
        return user != null && user.getPassword().equals(password);
    }
}
