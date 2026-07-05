package com.gym.payment;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.gym.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    @Value("${app.gym.name}")
    private String gymName;

    public byte[] generate(Payment payment, User member) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 60, 60);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            Paragraph title = new Paragraph(gymName, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph("Payment Invoice", boldFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.addCell(borderless("Invoice No: " + payment.getInvoiceNumber(), boldFont));
            header.addCell(borderless("Date: " + payment.getPaymentDate(), normalFont));
            header.addCell(borderless("Member: " + member.getFullName(), normalFont));
            header.addCell(borderless("Member ID: " + member.getId(), normalFont));
            document.add(header);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            addRow(table, "Plan", payment.getPlanNameSnapshot(), boldFont, normalFont);
            addRow(table, "Amount Paid", format(payment.getAmount()), boldFont, normalFont);
            addRow(table, "Discount", format(payment.getDiscount()), boldFont, normalFont);
            addRow(table, "GST", format(payment.getGst()), boldFont, normalFont);
            addRow(table, "Remaining Balance", format(payment.getRemainingBalance()), boldFont, normalFont);
            addRow(table, "Payment Mode", payment.getPaymentMode().name(), boldFont, normalFont);
            addRow(table, "Transaction ID", payment.getTransactionId() != null ? payment.getTransactionId() : "-", boldFont, normalFont);
            document.add(table);

            Paragraph footer = new Paragraph("\nThank you for choosing " + gymName + "!", normalFont);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Failed to generate invoice PDF: " + e.getMessage());
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        table.addCell(new PdfPCell(new Phrase(label, labelFont)));
        table.addCell(new PdfPCell(new Phrase(value, valueFont)));
    }

    private PdfPCell borderless(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private String format(BigDecimal value) {
        return value == null ? "0.00" : "Rs. " + value.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
