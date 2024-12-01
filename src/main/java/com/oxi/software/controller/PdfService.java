package com.oxi.software.controller;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.oxi.software.dto.PdfOrderDTO;
import com.oxi.software.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public String generateOrderPdf(PdfOrderDTO orderDTO) throws IOException {
        String filePath = "order_details_" + orderDTO.getId() + ".pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Establecer márgenes
        document.setMargins(36, 36, 36, 36); // margen superior, derecho, inferior, izquierdo

        // Agregar un título con estilo
        document.add(new Paragraph("Order Details")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Información de la orden
        document.add(new Paragraph("Order ID: " + orderDTO.getId())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMarginBottom(5));

        document.add(new Paragraph("Customer Name: " + orderDTO.getCustomerName())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMarginBottom(5));

        document.add(new Paragraph("Customer Email: " + orderDTO.getCustomerEmail())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMarginBottom(5));

        document.add(new Paragraph("Customer Address: " + orderDTO.getCustomerAddress())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMarginBottom(15));

        // Crear tabla para los productos
        Table table = new Table(4); // 4 columnas: Producto, Precio, Cantidad, Total
        table.setWidth(500);  // O establecer el ancho específico que desees para la tabla

        // Ajustar el ancho de las columnas para que la tabla ocupe el 100% del ancho disponible
        table.addHeaderCell(new Cell().add(new Paragraph("Product Name").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12)));
        table.addHeaderCell(new Cell().add(new Paragraph("Price").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12)));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12)));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12)));

        // Agregar filas de productos
        for (ProductDTO product : orderDTO.getProductList()) {
            table.addCell(new Paragraph(product.getName()).setFontSize(10));
            table.addCell(new Paragraph("$" + product.getPrice()).setFontSize(10));
            table.addCell(new Paragraph(String.valueOf(product.getQuantity())).setFontSize(10));
            table.addCell(new Paragraph("$" + (product.getPrice() * product.getQuantity())).setFontSize(10));
        }

        // Agregar tabla al documento
        document.add(table);


        // Agregar total
        document.add(new Paragraph("Total: $" + orderDTO.getTotalAmount())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));

        // Cerrar el documento
        document.close();

        return filePath;
    }
}
