// Deontae Cocroft | 2024 May 4th
package com;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Code for PostgresSQL database connection
public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:postgresql://localhost:5432/test";
        String username = "postgres";
        String password = "Char8305";
        
        return DriverManager.getConnection(jdbcURL, username, password);
    }
}