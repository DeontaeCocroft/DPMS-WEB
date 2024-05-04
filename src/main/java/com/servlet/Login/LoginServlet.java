// Deontae Cocroft | 2024 May 4th
package com.servlet.Login;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.DatabaseConnection;

// Method that validates users credentials to provide access to web application.
public class LoginServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get username and password from front end.
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {

            // Execute SQL Query to find credential match.
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM login WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            // Writes success or failure depending on credentials. This result is sent to JS.
            if (rs.next()) {
                response.getWriter().write("SUCCESS");
            } else {
                response.getWriter().write("FAILURE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("ERROR");
        }
    }
}
