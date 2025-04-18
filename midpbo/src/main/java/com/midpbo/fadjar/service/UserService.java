package com.midpbo.fadjar.service;

import com.midpbo.fadjar.model.User;
import com.midpbo.fadjar.util.SecurityUtils;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static final Map<String, User> users = new HashMap<>();
    
    public static boolean registerUser(String username, String fullName, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, fullName, SecurityUtils.hashPassword(password)));
        return true;
    }
    
    public static User authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) return null;
        return SecurityUtils.checkPassword(password, user.getHashedPassword()) ? user : null;
    }
}