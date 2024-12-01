package com.oxi.software.controller;

//import com.itextpdf.*;
//
//import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
//import com.oxi.software.dto.PdfOrderDTO;
//import com.oxi.software.dto.ProductDTO;
//import org.springframework.stereotype.Service;
//
//import java.io.FileOutputStream;
//import java.io.IOException;

//@Service
//public class PdfService {
//
//    public void generateOrderPdf(PdfOrderDTO orderDTO) throws IOException {
//        // Crear el documento PDF
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("order_details_" + orderDTO.getId() + ".pdf"));
//        document.open();
//
//        // Agregar contenido al documento (por ejemplo, detalles de la orden)
//        document.add(new Paragraph("Order ID: " + orderDTO.getId()));
//        document.add(new Paragraph("Customer Name: " + orderDTO.getCustomerName()));
//        document.add(new Paragraph("Customer Email: " + orderDTO.getCustomerEmail()));
//        document.add(new Paragraph("Customer Address: " + orderDTO.getCustomerAddress()));
//
//        // Lista de productos
//        document.add(new Paragraph("Items:"));
//        for (ProductDTO product : orderDTO.getProductList()) {
//            document.add(new Paragraph(product.getName() + " - $" + product.getPrice() + " x " + product.getQuantity()));
//        }
//
//        // Total de la orden
//        document.add(new Paragraph("Total: $" + orderDTO.getTotalAmount()));
//
//        // Cerrar el documento
//        document.close();
//    }
//}
