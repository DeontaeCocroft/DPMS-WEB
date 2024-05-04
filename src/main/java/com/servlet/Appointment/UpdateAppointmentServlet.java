// Deontae Cocroft | 2024 May 4th
package com.servlet.Appointment;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DatabaseConnection;

//Code that marks appointments as canceled or not canceled.
public class UpdateAppointmentServlet  extends HttpServlet{
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Fetch HTML field information from JS
        String isCanceledString = request.getParameter("canceled");
        Boolean isCanceled = Boolean.valueOf(isCanceledString);

        // Set as variables for parsing
        int appointmentId = 0;

        // Parsing appointment ID
        String appointmentIdString = request.getParameter("appointmentId");
        if (appointmentIdString != null && !appointmentIdString.isEmpty()) {
            try {
                appointmentId = Integer.parseInt(appointmentIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            // If appointment ID is not provided, return bad request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Executes Update statement
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE appointment SET canceled = ? WHERE appointment_id = ?")) {

            // Gets parameters from above to fill question marks with data.
            pstmt.setBoolean(1, isCanceled);
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    
}
