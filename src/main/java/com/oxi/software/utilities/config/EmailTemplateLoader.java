package com.oxi.software.utilities.config;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EmailTemplateLoader {
    private final TemplateEngine templateEngine;

    public EmailTemplateLoader(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateDeliveryEmail(String clientName, Long orderId, String domiciliaryName) {
        Context context = new Context();
        context.setVariable("clientName", clientName);
        context.setVariable("orderId", orderId);
        context.setVariable("domiciliaryName", domiciliaryName);
        context.setVariable("estimatedTime", LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));

        return templateEngine.process("delivery-status", context);
    }
}