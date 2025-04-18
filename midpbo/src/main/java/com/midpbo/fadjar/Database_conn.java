package com.midpbo.fadjar;

import java.sql.Connection;
import java.sql.Statement;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_conn {
    private static final String URL =  "jdbc:sqlite:Database.db";

    public static Connection connect(){
        try{
            return DriverManager.getConnection(URL);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void runSQLFile(String filePath) {
        try (Connection conn = connect();
             BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Statement stmt = conn.createStatement();
            String line;
            StringBuilder sql = new StringBuilder();

            // Membaca file line-by-line dan menggabungkan perintah SQL
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }

            // Menjalankan query SQL
            stmt.execute(sql.toString());
        } catch (IOException | SQLException e) {
            System.out.println("Error running SQL file: " + e.getMessage());
        }
    }
}
