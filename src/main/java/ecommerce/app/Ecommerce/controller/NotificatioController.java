package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.NotificationDto;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificatioController {
    private NotificationService notificationService;
    private UserRepository userRepository;

    public NotificatioController(NotificationService notificationService , UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication auth){
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String email = jwtAuth.getToken().getSubject();

            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found")
                    )
                    .getId();
        }

        throw new IllegalStateException("JWT authentication required");
    }

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getNotification(@RequestParam(defaultValue = "0") int page , @RequestParam(defaultValue = "10") int size ,
                                                                 Authentication auth){
        return  ResponseEntity.ok(notificationService.getNotifications(page , size , currentUserId(auth)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unReadNotification(Authentication auth){
        return ResponseEntity.ok(notificationService.unReadNotification(currentUserId(auth)));
    }

    @PatchMapping("read/{id}")
    public ResponseEntity<Void> markReadNotification(@PathVariable Long id , Authentication auth){
        notificationService.markReadNotification(id , currentUserId(auth));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("read/all")
    public ResponseEntity<Void> markReadAllNotification(Authentication auth){
        notificationService.markReadAllNotification(currentUserId(auth));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("resolve/{id}")
    public ResponseEntity<Void> resolveNotification(@PathVariable Long id , Authentication auth){
        notificationService.resolveNotification(id , currentUserId(auth));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("resolve/all")
    public ResponseEntity<Void> resolveAllNotification(Authentication auth){
        notificationService.resolveAllNotification(currentUserId(auth));
        return ResponseEntity.ok().build();
    }

}
