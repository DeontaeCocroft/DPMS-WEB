// Deontae Cocroft | 2024 May 4th
package com.servlet.Billing;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;
import com.DatabaseConnection;
import java.io.*;
import java.sql.*;

// Login for billing window
public class BillingServlet extends HttpServlet {
    
    // Method that fetches table data and allows users to search bills using certain parameters.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // JSON array to hold data.
        JSONArray jsonArray = new JSONArray();
        
        // Get appointmentId and billId for searching bills.
        String appointmentIDString = request.getParameter("appointmentID");
        String billIDString = request.getParameter("billID");

        // Set as variables for parsing.
        int appointmentID = 0;
        int billID = 0;
    
        // Checks to see if appointmentId available and parses to int for database.
        if (appointmentIDString != null && !appointmentIDString.isEmpty()) {
            try {
                appointmentID = Integer.parseInt(appointmentIDString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Checks to see if BillId available and parses to int for database.
        if (billIDString != null && !billIDString.isEmpty()) {
            try {
                billID = Integer.parseInt(billIDString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Part of query to select.
        String query = "SELECT bill.bill_id, bill.appointment_id, bill.notes, bill.is_paid, " +
                       "appointment.procedure_occurrences, appointment.procedure_id, procedure.price, " +
                       "(appointment.procedure_occurrences * procedure.price) AS total " +
                       "FROM bill " +
                       "INNER JOIN appointment ON bill.appointment_id = appointment.appointment_id " +
                       "INNER JOIN procedure ON appointment.procedure_id = procedure.procedure_id";
    
        boolean isFirstParameter = true;
        
        // If appointmentID or billId have a value then they will be added to the query.
        if (appointmentID != 0 || billID != 0) {
            query += isFirstParameter ? " WHERE" : " AND";
            if (appointmentID != 0) {
                query += " appointment.appointment_id = " + appointmentID;
                isFirstParameter = false;
            }
            if (billID != 0) {
                query += isFirstParameter ? " bill.bill_id = " + billID : " AND bill.bill_id = " + billID;
            }
        }
    
        // Adds the order by to the query after all other data is added
        query += " ORDER BY bill.bill_id DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             // Executes built query
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Uses JSON to pair JS fields with database columns for fetching data.
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("billId", rs.getString("bill_id"));
                jsonObject.put("appointmentId", rs.getString("appointment_id"));
                jsonObject.put("notes", rs.getString("notes"));
                jsonObject.put("isPaid", rs.getString("is_paid"));
                jsonObject.put("procedureOccurrences", rs.getString("procedure_occurrences"));
                jsonObject.put("procedureId", rs.getString("procedure_id"));
                jsonObject.put("price", rs.getString("price"));
                jsonObject.put("total", rs.getString("total"));
                jsonArray.put(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }

    // Method to add new patients to database
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Fetch HTML field information from JS
        String isPaidString = request.getParameter("isPaid");
        Boolean isPaid = Boolean.valueOf(isPaidString);
        String notes = request.getParameter("notes");
       
        // There are set to variables for parsing
        int appointmentId = 0;
    
        // Parsing appointmentId
        String appointmentIdString = request.getParameter("appointmentId");
        if (appointmentIdString != null && !appointmentIdString.isEmpty()) {
            try {
                appointmentId = Integer.parseInt(appointmentIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL query to insert bill information into database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bill (appointment_id, is_paid, notes)  VALUES (?, ?, ?)")) {
            
            // Gets parameters from above to fill question marks with data.
            pstmt.setInt(1, appointmentId);
            pstmt.setBoolean(2, isPaid);
            pstmt.setString(3, notes);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // Method deletes bills from database.
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set as variable for parsing
        int billId = 0;
    
        // Parsing bill ID
        String billIdString = request.getParameter("billId");
        if (billIdString != null && !billIdString.isEmpty()) {
            try {
                billId = Integer.parseInt(billIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL statement to delete bill from database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM bill WHERE bill_id = ?")) {
            
            // Gets parameter from above to fill question mark with data.
            pstmt.setInt(1, billId);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    } 
}
