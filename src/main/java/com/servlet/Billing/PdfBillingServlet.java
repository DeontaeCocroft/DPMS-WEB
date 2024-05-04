// Deontae Cocroft | 2024 May 4th
package com.servlet.Billing;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.json.JSONObject;

import com.DatabaseConnection;

// Method that collects data for PDF
public class PdfBillingServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the bill ID from the request
        String billIdString = request.getParameter("billId");

        // Validate the bill ID
        if (billIdString == null || billIdString.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int billId;
        try {
            billId = Integer.parseInt(billIdString);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Query to retrieve bill data along with patient and procedure information
        String sql = "SELECT bill.bill_id, bill.appointment_id, bill.notes, bill.is_paid, " +
                     "appointment.procedure_occurrences, appointment.procedure_id, procedure.price, " +
                     "(appointment.procedure_occurrences * procedure.price) AS total, " +
                     "patient.patient_id, patient.first_name, patient.last_name, " +
                     "patient.date_of_birth, patient.gender, patient.address, " +
                     "patient.city, patient.state, patient.zip_code, " +
                     "patient.insurance_company, patient.insurance_number, " +
                     "procedure.name AS procedure_name, procedure.notes AS procedure_notes " +
                     "FROM bill " +
                     "INNER JOIN appointment ON bill.appointment_id = appointment.appointment_id " +
                     "INNER JOIN procedure ON appointment.procedure_id = procedure.procedure_id " +
                     "INNER JOIN patient ON appointment.patient_id = patient.patient_id " +
                     "WHERE bill.bill_id = ? " +
                     "ORDER BY bill.bill_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the bill ID parameter in the SQL query
            pstmt.setInt(1, billId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    //JSON to hold the data.
                    JSONObject billData = new JSONObject();
                    billData.put("billId", rs.getInt("bill_id"));
                    billData.put("appointmentId", rs.getInt("appointment_id"));
                    String notes = rs.getString("notes");
                    if (notes != null && !notes.isEmpty()) {
                        billData.put("notes", notes);
                    } else {
                        billData.put("notes", "");
                    }
                    billData.put("isPaid", rs.getBoolean("is_paid"));
                    billData.put("procedureOccurrences", rs.getInt("procedure_occurrences"));
                    billData.put("procedureId", rs.getInt("procedure_id"));
                    billData.put("price", rs.getDouble("price"));
                    billData.put("total", rs.getDouble("total"));
                    billData.put("patientId", rs.getInt("patient_id"));
                    billData.put("firstName", rs.getString("first_name"));
                    billData.put("lastName", rs.getString("last_name"));
                    billData.put("dateOfBirth", rs.getString("date_of_birth"));
                    billData.put("gender", rs.getString("gender"));
                    billData.put("address", rs.getString("address"));
                    billData.put("city", rs.getString("city"));
                    billData.put("state", rs.getString("state"));
                    billData.put("zipCode", rs.getString("zip_code"));
                    billData.put("insuranceCompany", rs.getString("insurance_company"));
                    billData.put("insuranceNumber", rs.getString("insurance_number"));
                    billData.put("procedureName", rs.getString("procedure_name"));
                    billData.put("procedureNotes", rs.getString("procedure_notes"));

                    // Generate the PDF document using the bill data
                    generatePdf(response, billData);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    // Method to generate the PDF document
    private void generatePdf(HttpServletResponse response, JSONObject billData) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            // Set custom font for PDF
            File fontFile = new File("fonts/Times New Roman.ttf");
            PDType0Font font = PDType0Font.load(document, fontFile);


            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Layout of the PDF and fetches information from JSON.
                contentStream.setFont(font, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 750);
                contentStream.showText("Detailed Bill Report");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Bill Number: " + billData.getInt("billId"));
                contentStream.newLine();
                contentStream.showText("Appointment Number: " + billData.getInt("appointmentId"));
                contentStream.newLine();
                contentStream.showText("Notes: " + billData.getString("notes"));
                contentStream.newLine();
                contentStream.showText("Is Paid: " + (billData.getBoolean("isPaid") ? "Yes" : "No"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Procedure: " + billData.getString("procedureName"));
                contentStream.newLine();
                contentStream.showText("Procedure Notes: ");
                String procedureNotes = billData.getString("procedureNotes");
                List<String> lines = splitText(procedureNotes, 80); // Stops this longer text from running off pdf
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
                contentStream.newLine();
                contentStream.showText("Price: " + billData.getDouble("price"));
                contentStream.showText(" | Occurrences: " + billData.getInt("procedureOccurrences"));
                contentStream.showText(" | Total: " + billData.getDouble("total"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Patient Number: " + billData.getInt("patientId"));
                contentStream.showText(" | Patient First Name: " + billData.getString("firstName") + " | Patient Last Name: " + billData.getString("lastName"));
                contentStream.showText(" | DOB: " + billData.getString("dateOfBirth"));
                contentStream.showText(" | Gender: " + billData.getString("gender"));
                contentStream.newLine();
                contentStream.showText("Address: " + billData.getString("address"));
                contentStream.showText(" | City: " + billData.getString("city"));
                contentStream.showText(" | State: " + billData.getString("state"));
                contentStream.showText(" | Zip Code: " + billData.getString("zipCode"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Insurance Company: " + billData.getString("insuranceCompany"));
                contentStream.showText(" | Insurance Policy Number: " + billData.getString("insuranceNumber"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Print Name: _____________________________________________________");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Sign Name: _____________________________________________________");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Date: ________________________________");

                contentStream.endText();
            }

            // Set the response headers for PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"billing_" + billData.getInt("billId") + ".pdf\"");

            // Save the PDF document to the output stream
            document.save(response.getOutputStream());

            
        }
    }   
        // List that is in charge of handling text to make it wrap for PDF document.
        @SuppressWarnings("unused")
        private List<String> splitText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (currentLine.length() + word.length() <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word + " ");
            }
        }
        lines.add(currentLine.toString().trim());
        return lines;
    }
        
}
