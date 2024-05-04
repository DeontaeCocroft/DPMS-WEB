// Deontae Cocroft | 2024 May 4th
package com.servlet.Patient;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.DatabaseConnection;

// Code that updates patient information in database.
public class UpdatePatientServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Fetch HTML field information from JS
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dateOfBirthString = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String zipCodeString = request.getParameter("zipCode");
        String insuranceNumberString = request.getParameter("insuranceNumber");
        String phoneNumberString = request.getParameter("phoneNumber");
        String insuranceCompany = request.getParameter("insuranceCompany");
        String notes = request.getParameter("notes");
        String xrayImages = request.getParameter("xrayImages");

        // Set as variables for parsing
        int patientId = 0;
        int zipCode = 0;
        long insuranceNumber = 0;
        long phoneNumber = 0;
    
        // Parsing patient ID
        String patientIdString = request.getParameter("patientId");
        if (patientIdString != null && !patientIdString.isEmpty()) {
            try {
                patientId = Integer.parseInt(patientIdString);
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
    
        // Parsing zip code
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

        // Executes Update statement and has logic to not inserts values of 0 for integers so values in database wont be wiped
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE patient SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), date_of_birth = COALESCE(?, date_of_birth), gender = COALESCE(?, gender), address = COALESCE(?, address), city = COALESCE(?, city), state = COALESCE(?, state), "
                    + (zipCode != 0 ? "zip_code = COALESCE(?, zip_code)," : "")
                    + "insurance_company = COALESCE(?, insurance_company), "
                    + (insuranceNumber != 0 ? "insurance_number = COALESCE(?, insurance_number)," : "")
                    + (phoneNumber != 0 ? "phone_number = COALESCE(?, phone_number)," : "")
                    + "notes = COALESCE(?, notes), xray_images = COALESCE(?, xray_images) WHERE patient_id = ?")) {
            
            int parameterIndex = 1;
            
            // Gets parameters from above to fill question marks with data.
            pstmt.setString(parameterIndex++, firstName);
            pstmt.setString(parameterIndex++, lastName);
            pstmt.setObject(parameterIndex++, dateOfBirth);
            pstmt.setString(parameterIndex++, gender);
            pstmt.setString(parameterIndex++, address);
            pstmt.setString(parameterIndex++, city);
            pstmt.setString(parameterIndex++, state);

            // If fields equal 0 they will be skipped.
            if (zipCode != 0) {
                pstmt.setInt(parameterIndex++, zipCode);
            }
            pstmt.setString(parameterIndex++, insuranceCompany);
            if (insuranceNumber != 0) {
                pstmt.setLong(parameterIndex++, insuranceNumber);
            }
            if (phoneNumber != 0) {
                pstmt.setLong(parameterIndex++, phoneNumber);
            }
            pstmt.setString(parameterIndex++, notes);
            pstmt.setString(parameterIndex++, xrayImages);
            pstmt.setInt(parameterIndex++, patientId);
            
            pstmt.executeUpdate();
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
