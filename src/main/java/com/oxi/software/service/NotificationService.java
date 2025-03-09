package com.oxi.software.service;

import com.oxi.software.entity.Delivery;
import com.oxi.software.entity.Notification;
import com.oxi.software.repository.NotificationRepository;
import com.oxi.software.utilities.config.EmailTemplateLoader;
import com.oxi.software.utilities.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MailService mailService;
    private final NotificationRepository notificationRepository;
    private final EmailTemplateLoader templateLoader;

    @Async
    public void sendDeliveryNotification(Delivery delivery) {
        try {
            String content = templateLoader.generateDeliveryEmail(
                    delivery.getOrder().getUser().getIndividual().getName(),
                    delivery.getOrder().getId(),
                    delivery.getDomiciliary().getIndividual().getName()
            );

            mailService.sendEmail(
                    delivery.getOrder().getUser().getIndividual().getEmail(),
                    "OXI - Tu pedido está en camino",
                    content
            );

            // Registrar en base de datos
            Notification notification = new Notification();
            notification.setType("EMAIL");
            notification.setContent(content);
            notification.setDelivery(delivery);
            notification.setSuccess(true);
            notificationRepository.save(notification);

        } catch (Exception e) {
            // Registrar fallo
            Notification notification = new Notification();
            notification.setType("EMAIL");
            notification.setContent("Falló el envío: " + e.getMessage());
            notification.setDelivery(delivery);
            notification.setSuccess(false);
            notificationRepository.save(notification);

            throw new CustomException("Error en notificación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
