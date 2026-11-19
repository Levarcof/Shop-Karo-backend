package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.entity.WebhookEvent;
import ecommerce.app.Ecommerce.repository.WebhookEventRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final WebhookEventRepository webhookEventRepository;
    private final PaymentService paymentService;

    public WebhookService(WebhookEventRepository webhookEventRepository, PaymentService paymentService) {
        this.webhookEventRepository = webhookEventRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public void handle(String eventId, String payload) {
        if (webhookEventRepository.existsById(eventId)) {
            log.info("Duplicate webhook skipped: {}", eventId);
            return;
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");
        log.info("Webhook received: event={} id={}", event, eventId);

        switch (event) {
            case "payment.captured" -> {
                JSONObject e = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");
                paymentService.confirmPayment(e.getString("order_id"), e.getString("id"), e.getLong("amount"));
            }
            case "payment.failed" -> log.warn("payment.failed event: {}", payload);
            default -> log.debug("Ignored event {}", event);
        }

        webhookEventRepository.save(new WebhookEvent(eventId, event, LocalDateTime.now()));
    }
}