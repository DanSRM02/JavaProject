package com.oxi.software.service;

import com.oxi.software.dto.MailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

@Service
public class MailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public MailService(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void sendEmailWithTemplate(MailDTO mailDTO, Map<String, Object> templateVariables) throws MessagingException {
        // Renderizar la plantilla
        Context context = new Context();
        context.setVariables(templateVariables);
        String htmlContent = templateEngine.process("order-details", context);

        // Crear y enviar el correo
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // Set email fields
        helper.setTo(mailDTO.getTo());
        helper.setSubject(mailDTO.getSubject());
        helper.setText(htmlContent, true);

        // Verificar si hay un archivo PDF adjunto
        if (mailDTO.getAttachment() != null) {
            File pdfFile = new File(mailDTO.getAttachment());
            if (pdfFile.exists()) {
                helper.addAttachment("OrderDetails.pdf", pdfFile);
            } else {
                throw new RuntimeException("PDF file not found: " + mailDTO.getAttachment());
            }
        }

        // Enviar el correo
        mailSender.send(mimeMessage);
    }
}

