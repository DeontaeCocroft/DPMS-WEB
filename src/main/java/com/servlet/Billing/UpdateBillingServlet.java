// Deontae Cocroft | 2024 May 4th
package com.servlet.Billing;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DatabaseConnection;

// Code that marks bills as paid or not paid
public class UpdateBillingServlet extends HttpServlet{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Fetch HTML field information from JS
        String isPaidString = request.getParameter("isPaid");
        Boolean isPaid = Boolean.valueOf(isPaidString);

        // Set as variables for parsing
        int billId = 0;
    
        // Parsing patient ID
        String billIdString = request.getParameter("billId");
        if (billIdString != null && !billIdString.isEmpty()) {
            try {
                billId = Integer.parseInt(billIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            // If patient ID is not provided return bad request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Executes Update statement
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE bill SET is_paid = ? WHERE bill_id = ?")) {
            
            // Gets parameters from above to fill question marks with data.
            pstmt.setBoolean(1, isPaid);
            pstmt.setInt(2, billId);
            pstmt.executeUpdate();
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
}
