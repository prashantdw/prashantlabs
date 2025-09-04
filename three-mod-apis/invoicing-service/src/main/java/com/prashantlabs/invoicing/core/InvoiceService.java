package com.prashantlabs.invoicing.core;

import com.prashantlabs.invoicing.web.InvoiceLine;
import com.prashantlabs.invoicing.web.InvoiceRequest;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class InvoiceService {
    private final Path dir;
    private final JavaMailSender mail;

    public InvoiceService(@Value("${app.invoices.dir}") String d, JavaMailSender mail) {
        this.dir = Path.of(d);
        this.mail = mail;
    }

    public Path generate(InvoiceRequest req) throws Exception {
        Files.createDirectories(dir);
        Path out = dir.resolve(req.invoiceNumber() + "-" + UUID.randomUUID() + ".pdf");
        try (OutputStream os = Files.newOutputStream(out)) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, os);
            doc.open();
            doc.add(new Paragraph("INVOICE #" + req.invoiceNumber(), new Font(Font.HELVETICA, 18, Font.BOLD)));
            doc.add(new Paragraph("Customer: " + req.customerName()));
            doc.add(new Paragraph("Email: " + req.customerEmail()));
            doc.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(4);
            table.addCell("Item");
            table.addCell("Qty");
            table.addCell("Unit Price");
            table.addCell("Line Total");
            java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
            for (InvoiceLine line : req.lines()) {
                BigDecimal lineTotal = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
                subtotal = subtotal.add(lineTotal);
                table.addCell(line.item());
                table.addCell(String.valueOf(line.quantity()));
                table.addCell(line.unitPrice().toPlainString());
                table.addCell(lineTotal.toPlainString());
            }
            doc.add(table);
            BigDecimal tax = subtotal.multiply(req.taxPercent()).divide(BigDecimal.valueOf(100));
            BigDecimal grand = subtotal.add(tax);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Subtotal: " + subtotal));
            doc.add(new Paragraph("Tax(" + req.taxPercent() + "%): " + tax));
            doc.add(new Paragraph("Grand Total: " + grand, new Font(Font.HELVETICA, 12, Font.BOLD)));
            doc.close();
        }
        return out;
    }

    public void emailInvoice(String to, Path pdf) throws Exception {
        var msg = mail.createMimeMessage();
        var helper = new MimeMessageHelper(msg, true);
        helper.setTo(to);
        helper.setSubject("Your Invoice");
        helper.setText("Please find attached invoice.");
        helper.addAttachment(pdf.getFileName().toString(), new FileSystemResource(new File(pdf.toString())));
        mail.send(msg);
    }
}
