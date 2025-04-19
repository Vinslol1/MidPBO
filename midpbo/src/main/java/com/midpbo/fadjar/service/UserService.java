package com.midpbo.fadjar.service;

import com.midpbo.fadjar.Database_conn;
import com.midpbo.fadjar.model.User;
import com.midpbo.fadjar.util.SecurityUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static final Map<String, User> users = new HashMap<>();

    
    public static boolean registerUser(String username, String fullName, String password) {

        
        String query = "INSERT INTO users (username, fullname, password) VALUES (?,?,?)";
        String check = "SELECT username FROM users WHERE username = ?";

        try (Connection conn = Database_conn.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             PreparedStatement pstmtCheck = conn.prepareStatement(check)) {
            
            pstmtCheck.setString(1, username);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next()){
                return false;
            }
                pstmt.setString(1, username);
                pstmt.setString(2, fullName);
                pstmt.setString(3, SecurityUtils.hashPassword(password));
                pstmt.executeUpdate();
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static User authenticate(String username, String password) {

        String query = "SELECT username, fullname, password FROM users WHERE username = ?";
        try (Connection conn = Database_conn.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, username);  
        ResultSet rs = pstmt.executeQuery(); 

        if (rs.next()) {
            String dbUsername = rs.getString("username");
            String fullName = rs.getString("fullname");
            String hashedPassword = rs.getString("password");

            if (SecurityUtils.checkPassword(password, hashedPassword)) {
                return new User(dbUsername, fullName, hashedPassword);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
        // try(Connection conn = Database_conn.connect();
        //     PreparedStatement pstmt = conn.prepareStatement(query)){
        //         User user = new User(username.getString("username"), fullname.getString("fullname"),password.getString("password"));
        //         if (user == null) return null;
        //         return SecurityUtils.checkPassword(password, user.getHashedPassword()) ? user : null;
        // }
        return null;
    }
}