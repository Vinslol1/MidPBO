package com.midpbo.fadjar.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.midpbo.fadjar.Database_conn;

public class LogService {
    public static void addLog(String username, String action) {
        try (Connection conn = Database_conn.connect()) {
            String sql = "INSERT INTO logs (username, action) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, action);
            stmt.executeUpdate();
            System.out.println("Log added: " + action);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
