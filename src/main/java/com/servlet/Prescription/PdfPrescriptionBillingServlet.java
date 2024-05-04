// Deontae Cocroft | 2024 May 4th
package com.servlet.Prescription;
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
public class PdfPrescriptionBillingServlet extends HttpServlet{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the prescription bill ID from the request
        String prescriptionBillIdString = request.getParameter("prescriptionBillId");
    
        // Validate the prescription bill ID
        if (prescriptionBillIdString == null || prescriptionBillIdString.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    
        int prescriptionBillId;
        try {
            prescriptionBillId = Integer.parseInt(prescriptionBillIdString);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    
        // Query to retrieve prescription bill data along with patient and prescription information
        String sql = "SELECT prescription_bill_info.prescription_bill_info_id, prescription_bill_info.patient_id, " +
                     "prescription.prescription_id, prescription_bill_info.quantity, " +
                     "prescription.price, (prescription.price * prescription_bill_info.quantity) AS total, " +
                     "patient.first_name, patient.last_name, patient.date_of_birth, patient.gender, patient.address, " +
                     "patient.city, patient.state, patient.zip_code, patient.insurance_company, patient.insurance_number, " +
                     "prescription.name AS prescription_name, prescription.notes AS prescription_notes " +
                     "FROM prescription_bill_info " +
                     "INNER JOIN prescription ON prescription_bill_info.prescription_id = prescription.prescription_id " +
                     "INNER JOIN patient ON prescription_bill_info.patient_id = patient.patient_id " +
                     "WHERE prescription_bill_info.prescription_bill_info_id = ? " +
                     "ORDER BY prescription_bill_info.prescription_bill_info_id DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the prescription bill ID parameter in the SQL query
            pstmt.setInt(1, prescriptionBillId);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //JSON to hold prescription bill data
                    JSONObject preBillData = new JSONObject();
                    preBillData.put("prescriptionBillId", rs.getInt("prescription_bill_info_id"));
                    preBillData.put("prescriptionId", rs.getInt("prescription_id"));
                    preBillData.put("prescriptionName", rs.getString("prescription_name"));
                    preBillData.put("prescriptionNotes", rs.getString("prescription_notes"));
                    preBillData.put("price", rs.getDouble("price"));
                    preBillData.put("quantity", rs.getInt("quantity"));
                    preBillData.put("total", rs.getDouble("total"));
                    preBillData.put("patientId", rs.getInt("patient_id"));
                    preBillData.put("firstName", rs.getString("first_name"));
                    preBillData.put("lastName", rs.getString("last_name"));
                    preBillData.put("dateOfBirth", rs.getString("date_of_birth"));
                    preBillData.put("gender", rs.getString("gender"));
                    preBillData.put("address", rs.getString("address"));
                    preBillData.put("city", rs.getString("city"));
                    preBillData.put("state", rs.getString("state"));
                    preBillData.put("zipCode", rs.getString("zip_code"));
                    preBillData.put("insuranceCompany", rs.getString("insurance_company"));
                    preBillData.put("insuranceNumber", rs.getString("insurance_number"));
    
                    // Generate the PDF document using the prescription bill data
                    generatePdf(response, preBillData);
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
private void generatePdf(HttpServletResponse response, JSONObject preBillData) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage();
        document.addPage(page);

        // Set custom font for PDF
        File fontFile = new File("fonts/Times New Roman.ttf");
        PDType0Font font = PDType0Font.load(document, fontFile);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Detailed Prescription Bill Report");
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Prescription Bill Number: " + preBillData.getInt("prescriptionBillId"));
            contentStream.newLine();
            contentStream.showText("Prescription Number: " + preBillData.getInt("prescriptionId"));
            contentStream.newLine();
            contentStream.showText("Prescription Name: " + preBillData.getString("prescriptionName"));
            contentStream.newLine();
            contentStream.showText("Prescription Notes: ");
            String prescriptionNotes = preBillData.getString("prescriptionNotes");
                List<String> lines = splitText(prescriptionNotes, 80); // Stops this longer text from running off pdf
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
            contentStream.showText("Price: " + preBillData.getDouble("price"));
            contentStream.newLine();
            contentStream.showText("Quantity: " + preBillData.getInt("quantity"));
            contentStream.newLine();
            contentStream.showText("Total: " + preBillData.getDouble("total"));
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Patient Number: " + preBillData.getInt("patientId"));
            contentStream.showText(" | Patient First Name: " + preBillData.getString("firstName") + " | Patient Last Name: " + preBillData.getString("lastName"));
            contentStream.showText(" | DOB: " + preBillData.getString("dateOfBirth"));
            contentStream.showText(" | Gender: " + preBillData.getString("gender"));
            contentStream.newLine();
            contentStream.showText("Address: " + preBillData.getString("address"));
            contentStream.showText(" | City: " + preBillData.getString("city"));
            contentStream.showText(" | State: " + preBillData.getString("state"));
            contentStream.showText(" | Zip Code: " + preBillData.getString("zipCode"));
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Insurance Company: " + preBillData.getString("insuranceCompany"));
            contentStream.showText(" | Insurance Policy Number: " + preBillData.getString("insuranceNumber"));
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
            contentStream.newLine();
            
            contentStream.endText();
        }

        // Set the response headers for PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"prescription_bill_" + preBillData.getInt("prescriptionBillId") + ".pdf\"");

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
