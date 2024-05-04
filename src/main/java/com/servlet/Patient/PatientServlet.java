// Deontae Cocroft | 2024 May 4th
package com.servlet.Patient;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.sql.*;

import com.DatabaseConnection;

//Login for patient window
public class PatientServlet extends HttpServlet {

    // Method that fetches table data and allows users to search patients using certain parameters.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // JSON array to hold data
        JSONArray jsonArray = new JSONArray();

        // Get firstName, lastName, and phoneNumber for searching patients.
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumberString = request.getParameter("phoneNumber");
        Long phoneNumber = null;

        // Checks to see if phoneNumber is available and parses to Long for database
        if (phoneNumberString != null && !phoneNumberString.isEmpty()) {
            try {
                phoneNumber = Long.parseLong(phoneNumberString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        // Part of query to select from patients
        String query = "SELECT * FROM patient";
        boolean isFirstParameter = true;

        // If firstName is available it is used to search patient and added to query
        if (firstName != null && !firstName.isEmpty()) {
            query += isFirstParameter ? " WHERE" : " AND";
            query += " first_name LIKE '%" + firstName + "%'";
            isFirstParameter = false;
        }

        // If lastName is available it is used to search patient and added to query
        if (lastName != null && !lastName.isEmpty()) {
            query += isFirstParameter ? " WHERE" : " AND";
            query += " last_name LIKE '%" + lastName + "%'";
            isFirstParameter = false;
        }

        // If phoneNumber is is available it is used to search patient and added to query
        if (phoneNumber != null) {
            query += isFirstParameter ? " WHERE" : " AND";
            query += " phone_number = " + phoneNumber;
        }

        // Adds the order by to the query after all other data is added
        query += " ORDER BY last_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             // Executes built query
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Uses JSON to pair JS fields with database columns for fetching data.
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("patientId", rs.getString("patient_id"));
                jsonObject.put("firstName", rs.getString("first_name"));
                jsonObject.put("lastName", rs.getString("last_name"));
                jsonObject.put("dateOfBirth", rs.getString("date_of_birth"));
                jsonObject.put("gender", rs.getString("gender"));
                jsonObject.put("address", rs.getString("address"));
                jsonObject.put("city", rs.getString("city"));
                jsonObject.put("state", rs.getString("state"));
                jsonObject.put("zipCode", rs.getInt("zip_code"));
                jsonObject.put("insuranceCompany", rs.getString("insurance_company"));
                jsonObject.put("insuranceNumber", rs.getLong("insurance_number"));
                jsonObject.put("phoneNumber", rs.getLong("phone_number"));
                jsonObject.put("notes", rs.getString("notes"));
                jsonObject.put("xrayImages", rs.getString("xray_images"));
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
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dateOfBirthString = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String insuranceCompany = request.getParameter("insuranceCompany");
        String notes = request.getParameter("notes");
        String xrayImages = request.getParameter("xrayImages");

        // There are set to variables for parsing
        int zipCode = 0;
        long insuranceNumber = 0;
        long phoneNumber = 0;

        // Parsing zip code
        String zipCodeString = request.getParameter("zipCode");
        if (zipCodeString != null && !zipCodeString.isEmpty()) {
            try {
                zipCode = Integer.parseInt(zipCodeString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Parsing insurance number
        String insuranceNumberString = request.getParameter("insuranceNumber");
        if (insuranceNumberString != null && !insuranceNumberString.isEmpty()) {
            try {
                insuranceNumber = Long.parseLong(insuranceNumberString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        // Parsing phone number
        String phoneNumberString = request.getParameter("phoneNumber");
        if (phoneNumberString != null && !phoneNumberString.isEmpty()) {
            try {
                phoneNumber = Long.parseLong(phoneNumberString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Parsing date of birth
        java.sql.Date dateOfBirth = null;
        if (dateOfBirthString != null && !dateOfBirthString.isEmpty()) {
            try {
                dateOfBirth = java.sql.Date.valueOf(dateOfBirthString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL query to insert patient information into database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO patient (first_name, last_name, date_of_birth, gender, address, city, state, zip_code, insurance_company, insurance_number, phone_number, notes, xray_images) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            
            // Gets parameters from above to fill question marks with data.
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setDate(3, dateOfBirth);
            pstmt.setString(4, gender);
            pstmt.setString(5, address);
            pstmt.setString(6, city);
            pstmt.setString(7, state);
            pstmt.setInt(8, zipCode);
            pstmt.setString(9, insuranceCompany);
            pstmt.setLong(10, insuranceNumber);
            pstmt.setLong(11, phoneNumber);
            pstmt.setString(12, notes);
            pstmt.setString(13, xrayImages);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // Method deletes patients from database.
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Set as variable for parsing
        int patientId = 0;
    
        // Parsing patient ID
        String patientIDString = request.getParameter("patientId");
        if (patientIDString != null && !patientIDString.isEmpty()) {
            try {
                patientId = Integer.parseInt(patientIDString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        // Executes SQL statement to delete patient from database.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM patient WHERE patient_id = ?")) {
            
            // Gets parameter from above to fill question mark with data.
            pstmt.setInt(1, patientId);
            pstmt.executeUpdate();
    
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
}

