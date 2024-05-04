// Deontae Cocroft | 2024 May 4th
package com.servlet.Appointment;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;
import com.DatabaseConnection;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

// logic for appointment window
public class AppointmentServlet extends HttpServlet{
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // JSON array to hold data
        JSONArray jsonArray = new JSONArray();
    
        // Get patient_id and appointment_id for searching appointments.
        String patientIdString = request.getParameter("patientId");
        String appointmentIdString = request.getParameter("appointmentId");
        int patientId = 0;
        int appointmentId = 0;
    
        // Checks to see if patientId is available and parses to int for database
        if (patientIdString != null && !patientIdString.isEmpty()) {
            try {
                patientId = Integer.parseInt(patientIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Checks to see if appointmentId is available and parses to int for database
        if (appointmentIdString != null && !appointmentIdString.isEmpty()) {
            try {
                appointmentId = Integer.parseInt(appointmentIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
    
        // Part of query to select from appointments
        String query = "SELECT appointment_id, patient_id, procedure_id, procedure_occurrences, dentist_id," +
                "dental_hygienist_id, dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes, canceled FROM appointment";
    
        boolean isFirstParameter = true;
    
        // If patientId is available it is used to search appointments and added to query
        if (patientId != 0) {
            query += isFirstParameter ? " WHERE" : " AND";
            query += " patient_id = " + patientId;
            isFirstParameter = false;
        }
    
        // If appointmentId is available it is used to search appointments and added to query
        if (appointmentId != 0) {
            query += isFirstParameter ? " WHERE" : " AND";
            query += " appointment_id = " + appointmentId;
        }
    
        // Adds the order by to the query after all other data is added
        query += " ORDER BY appointment_date DESC, appointment_time DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             // Executes built query
             ResultSet rs = stmt.executeQuery(query)) {
    
            // Uses JSON to pair JS fields with database columns for fetching data.
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appointmentId", rs.getInt("appointment_id"));
                jsonObject.put("patientId", rs.getInt("patient_id"));
                jsonObject.put("procedureId", rs.getInt("procedure_id"));
                jsonObject.put("procedureOccurrences", rs.getInt("procedure_occurrences"));
                jsonObject.put("dentistId", rs.getInt("dentist_id"));
                jsonObject.put("dentalHygienistId", rs.getInt("dental_hygienist_id"));
                jsonObject.put("dentalAssistantId", rs.getInt("dental_assistant_id"));
                jsonObject.put("dentalSurgeonId", rs.getInt("dental_surgeon_id"));
                jsonObject.put("appointmentDate", rs.getString("appointment_date"));
                jsonObject.put("appointmentTime", rs.getString("appointment_time"));
                jsonObject.put("notes", rs.getString("notes"));
                jsonObject.put("canceled", rs.getBoolean("canceled"));
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

    //Method to add appointments to database
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Variables for parsing
        int patientId = 0;
        int procedureId = 0;
        int procedureOccurrences = 0;
        int dentistId = 0;
        int dentalHygienistId = 0;
        int dentalAssistantId = 0;
        int dentalSurgeonId = 0;
        boolean canceled;
    
        // Retrieve parameters from request
        String patientIdString = request.getParameter("patientId");
        String procedureIdString = request.getParameter("procedureId");
        String procedureOccurrencesString = request.getParameter("procedureOccurrences");
        String dentistIdString = request.getParameter("dentistId");
        String dentalHygienistIdString = request.getParameter("dentalHygienistId");
        String dentalAssistantIdString = request.getParameter("dentalAssistantId");
        String dentalSurgeonIdString = request.getParameter("dentalSurgeonId");
        String appointmentDateString = request.getParameter("appointmentDate");
        String appointmentTimeString = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        String canceledString = request.getParameter("canceled");
    
        // Check if any of the required parameters are missing
        if (patientIdString == null || procedureIdString == null || procedureOccurrencesString == null ||
            dentistIdString == null || dentalHygienistIdString == null || appointmentDateString == null ||
            appointmentTimeString == null || canceledString == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    
        // Parse parameters to appropriate types
        try {
            patientId = Integer.parseInt(patientIdString);
            procedureId = Integer.parseInt(procedureIdString);
            procedureOccurrences = Integer.parseInt(procedureOccurrencesString);
            dentistId = Integer.parseInt(dentistIdString);
            dentalHygienistId = Integer.parseInt(dentalHygienistIdString);
            dentalAssistantId = dentalAssistantIdString.isEmpty() ? 0 : Integer.parseInt(dentalAssistantIdString);
            dentalSurgeonId = dentalSurgeonIdString.isEmpty() ? 0 : Integer.parseInt(dentalSurgeonIdString);
            canceled = Boolean.parseBoolean(canceledString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    
        // Parse appointmentDate to LocalDate
        LocalDate appointmentDate = LocalDate.parse(appointmentDateString);
    
        // Parse appointmentTime to LocalTime
        LocalTime appointmentTime = LocalTime.parse(appointmentTimeString);
    
        // Calculate the time 30 minutes before and after the new appointment
        LocalTime thirtyMinutesBefore = appointmentTime.minusMinutes(29);
        LocalTime thirtyMinutesAfter = appointmentTime.plusMinutes(29);
    
        // Prepare SQL query
        String sqlOverlapCheck = "SELECT COUNT(*) FROM appointment WHERE appointment_date = ? AND " +
                                 "(dentist_id = ? OR dental_hygienist_id = ? OR dental_assistant_id = ? OR dental_surgeon_id = ?) AND " +
                                 "((appointment_time >= ? AND appointment_time <= ?) OR (appointment_time <= ? AND appointment_time >= ?))";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statementOverlapCheck = conn.prepareStatement(sqlOverlapCheck)) {
    
            statementOverlapCheck.setDate(1, Date.valueOf(appointmentDate));
            statementOverlapCheck.setInt(2, dentistId);
            statementOverlapCheck.setInt(3, dentalHygienistId);
            statementOverlapCheck.setInt(4, dentalAssistantId);
            statementOverlapCheck.setInt(5, dentalSurgeonId);
            statementOverlapCheck.setTime(6, Time.valueOf(thirtyMinutesBefore));
            statementOverlapCheck.setTime(7, Time.valueOf(thirtyMinutesAfter));
            statementOverlapCheck.setTime(8, Time.valueOf(thirtyMinutesBefore));
            statementOverlapCheck.setTime(9, Time.valueOf(thirtyMinutesAfter));
    
            ResultSet resultSetOverlapCheck = statementOverlapCheck.executeQuery();
            resultSetOverlapCheck.next();
            int count = resultSetOverlapCheck.getInt(1);
    
            if (count != 0) {
                // Conflicting appointment found
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    
        // If no conflicts, proceed with saving the appointment
        String insertQuery = "INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id," +
                             "dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes, canceled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, patientId);
            insertStmt.setInt(2, procedureId);
            insertStmt.setInt(3, procedureOccurrences);
            insertStmt.setInt(4, dentistId);
            insertStmt.setInt(5, dentalHygienistId);
            if (dentalAssistantId != 0) {
                insertStmt.setInt(6, dentalAssistantId);
            } else {
                insertStmt.setNull(6, Types.INTEGER);
            }
            if (dentalSurgeonId != 0) {
                insertStmt.setInt(7, dentalSurgeonId);
            } else {
                insertStmt.setNull(7, Types.INTEGER);
            }
            insertStmt.setDate(8, Date.valueOf(appointmentDate));
            insertStmt.setTime(9, Time.valueOf(appointmentTime));
            insertStmt.setString(10, notes);
            insertStmt.setBoolean(11, canceled);
    
            // Execute the insertion query
            insertStmt.executeUpdate();
    
            // Return success status
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    //Method deletes appointments from database.
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Set as variable for parsing
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
        }
        
        //Executes SQL statement to delete appointment from database.
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM appointment WHERE appointment_id = ?")) {
            
            //Gets parameter from above to fill question mark with data.
            pstmt.setInt(1, appointmentId);
            pstmt.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
