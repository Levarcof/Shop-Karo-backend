package ecommerce.app.Ecommerce.controller;

import com.razorpay.Utils;
import ecommerce.app.Ecommerce.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/razorpay")
public class RazorpayWebhookController {

    private final WebhookService webhookService;
    private static final Logger log = LoggerFactory.getLogger(RazorpayWebhookController.class);
    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public RazorpayWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload ,    @RequestHeader("X-Razorpay-Signature") String signature,
                                          @RequestHeader("X-Razorpay-Event-Id") String eventId){
        try{
            if (!Utils.verifyWebhookSignature(payload, signature, webhookSecret)){
                log.warn("Webhook invalid signature");
                return  ResponseEntity.badRequest().body("Invalid Signature");
            }

            if (eventId == null || eventId.isBlank()) {
                eventId = UUID.nameUUIDFromBytes(payload.getBytes()).toString();
            }
            webhookService.handle(eventId , payload);
            return  ResponseEntity.ok("ok");
        }
        catch (Exception e){
            log.error("Webhook processing failed", e);
            return ResponseEntity.status(500).body("error"); // 500 par Razorpay baad me retry karega

        }
    }
}
