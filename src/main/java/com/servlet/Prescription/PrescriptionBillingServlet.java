// Deontae Cocroft | 2024 May 4th
package com.servlet.Prescription;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import com.DatabaseConnection;

public class PrescriptionBillingServlet extends HttpServlet{
    // Method that fetches table data and allows users to search prescription bills using certain parameters.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // JSON array to hold data.
        JSONArray jsonArray = new JSONArray();
    
        // Get patient id and prescription bill info id for searching prescription bills.
        String patientIDString = request.getParameter("patientId");
        String prescriptionBillInfoIdString = request.getParameter("prescriptionBillId");
    
        int patientID = 0;
        int prescriptionBillInfoID = 0;
    
        // Checks to see if patientID available and parses to int for database.
        if (patientIDString != null && !patientIDString.isEmpty()) {
            try {
                patientID = Integer.parseInt(patientIDString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Checks to see if prescriptionBillInfoID available and parses to int for database.
        if (prescriptionBillInfoIdString != null && !prescriptionBillInfoIdString.isEmpty()) {
            try {
                prescriptionBillInfoID = Integer.parseInt(prescriptionBillInfoIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Part of query to select.
        String query = "SELECT prescription_bill_info.prescription_bill_info_id, prescription_bill_info.patient_id, " +
                       "prescription.prescription_id, prescription_bill_info.quantity, " +
                       "(prescription.price * prescription_bill_info.quantity) AS total " +
                       "FROM prescription_bill_info " +
                       "INNER JOIN prescription ON prescription_bill_info.prescription_id = prescription.prescription_id";
    
        boolean isFirstParameter = true;
    
        // If patientID or prescriptionBillInfoID have a value then they will be added to the query.
        if (patientID != 0 || prescriptionBillInfoID != 0) {
            query += isFirstParameter ? " WHERE" : " AND";
            if (patientID != 0) {
                query += " prescription_bill_info.patient_id = " + patientID;
                isFirstParameter = false;
            }
            if (prescriptionBillInfoID != 0) {
                query += isFirstParameter ? " prescription_bill_info.prescription_bill_info_id = " + prescriptionBillInfoID : " AND prescription_bill_info.prescription_bill_info_id = " + prescriptionBillInfoID;
            }
        }
    
        // Adds the order by to the query after all other data is added
        query += " ORDER BY prescription_bill_info.prescription_bill_info_id DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Uses JSON to pair JS fields with database columns for fetching data.
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("prescriptionBillId", rs.getString("prescription_bill_info_id"));
                jsonObject.put("patientId", rs.getString("patient_id"));
                jsonObject.put("prescriptionId", rs.getString("prescription_id"));
                jsonObject.put("quantity", rs.getString("quantity"));
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
       
        // Variables for parsing
        int patientId = 0;
        int prescriptionId = 0;
        int quantity = 0;
    
        // Parsing patientId
        String patientIdString = request.getParameter("patientId");
        if (patientIdString != null && !patientIdString.isEmpty()) {
            try {
                patientId = Integer.parseInt(patientIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Parsing prescriptionId
        String prescriptionIdString = request.getParameter("prescriptionId");
        if (prescriptionIdString != null && !prescriptionIdString.isEmpty()) {
            try {
                prescriptionId = Integer.parseInt(prescriptionIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Parsing quantity
        String quantityString = request.getParameter("quantity");
        if (quantityString != null && !quantityString.isEmpty()) {
            try {
                quantity = Integer.parseInt(quantityString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL query to check adjusted stock
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT (prescription.items_in_stock - (SELECT COALESCE(SUM(prescription_bill_info.quantity), 0) " +
                                "FROM prescription_bill_info " +
                                "WHERE prescription_bill_info.prescription_id = ?)) as adjusted_stock " +
                                "FROM prescription WHERE prescription.prescription_id = ?")) {
            
            pstmt.setInt(1, prescriptionId);
            pstmt.setInt(2, prescriptionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int adjustedStock = rs.getInt("adjusted_stock");
                if (adjustedStock - quantity < 0) {
                    // Insufficient stock, return error
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else {
                // Prescription not found, return error
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        // Executes SQL query to insert bill information into database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO prescription_bill_info (patient_id, prescription_id, quantity) VALUES (?, ?, ?)")) {
            
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, prescriptionId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // Method deletes prescription bills from database.
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set as variable for parsing
        int prescriptionBillId = 0;
    
        // Parsing patient ID
        String prescriptionBillIdString = request.getParameter("prescriptionBillId");
        if (prescriptionBillIdString != null && !prescriptionBillIdString.isEmpty()) {
            try {
                prescriptionBillId = Integer.parseInt(prescriptionBillIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL statement to delete bill from database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM prescription_bill_info WHERE prescription_bill_info_id = ?")) {
            
            // Gets parameter from above to fill question mark with data.
            pstmt.setInt(1, prescriptionBillId);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
}
