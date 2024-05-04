// Deontae Cocroft | 2024 May 4th
package com.servlet.Appointment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

// Code
public class ProcedureServlet extends HttpServlet {
    
    // Method that fetches table data for procedures.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // JSON array to hold data.
        JSONArray jsonArray = new JSONArray();

        // Query
        String query = "SELECT * FROM procedure ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            // Uses JSON to pair JS fields with database columns for fetching data.
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("procedureId", rs.getString("procedure_id"));
                jsonObject.put("name", rs.getString("name"));
                jsonObject.put("notes", rs.getString("notes"));
                jsonObject.put("price", rs.getString("price"));
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

    
}
