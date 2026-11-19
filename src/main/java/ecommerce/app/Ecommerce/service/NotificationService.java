package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.NotificationDto;
import ecommerce.app.Ecommerce.entity.Notification;
import ecommerce.app.Ecommerce.enums.NotificationType;
import ecommerce.app.Ecommerce.enums.RecipientType;
import ecommerce.app.Ecommerce.enums.ReferenceType;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.NotificationRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void createNotification(NotificationType type, Long recipientId , RecipientType recipientType , ReferenceType referenceType , Long referenceId
            , String title, String message , Integer expiryDays){
        if (expiryDays != null && expiryDays < 0) {
            throw new IllegalArgumentException(
                    "Expiry days cannot be negative"
            );
        }
        Notification notification = new Notification();
        notification.setType(type);
        notification.setRecipientId(recipientId);
        notification.setRecipientType(recipientType);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());

        LocalDateTime expireTime = expiryDays == null ? null : LocalDateTime.now().plusDays(expiryDays);
        notification.setExpiresAt(expireTime);

        notificationRepository.save(notification);
    }

    @Transactional
    public void resolveLowStockNotification(Long productId){
        Optional<Notification> notification = notificationRepository.findByReferenceTypeAndReferenceIdAndTypeAndIsResolvedFalse(
                ReferenceType.PRODUCT,
                productId,
                NotificationType.LOW_STOCK
        );

        if(notification.isPresent()){
            notification.get().setResolved(true);
            notification.get().setResolvedAt(LocalDateTime.now());
        }

    }

    @Transactional
    public  void  resolveOutOfStockNotification(Long productId){
        Optional<Notification> notification = notificationRepository.findByReferenceTypeAndReferenceIdAndTypeAndIsResolvedFalse(
                ReferenceType.PRODUCT,
                productId,
                NotificationType.OUT_OF_STOCK
        );

        if(notification.isPresent()){
            notification.get().setResolved(true);
            notification.get().setResolvedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public  Page<NotificationDto> getNotifications(int page, int size, Long userId) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(page , size, Sort.by(Sort.Direction.DESC ,    "createdAt")
                .and(Sort.by(Sort.Direction.DESC ,    "id")));

        Page<Notification> notifications = notificationRepository.findByRecipientIdAndRecipientType(userId , RecipientType.USER , pageable);

        return notifications.map(this::getNotificationDto);
    }

    private NotificationDto getNotificationDto(Notification notification){
        return new NotificationDto(notification.getId(),notification.getType() , notification.getRecipientId(),  notification.getRecipientType() ,
                notification.getReferenceType(), notification.getReferenceId(), notification.getTitle() , notification.getMessage() ,
                notification.isRead() , notification.getReadAt() , notification.getCreatedAt() , notification.isResolved(),
                notification.getResolvedAt());

    }

    public  Long unReadNotification(Long userId) {
        if(userId == null){
            throw  new IllegalArgumentException("user id can not be null");
        }
        return notificationRepository.countByRecipientIdAndRecipientTypeAndIsReadFalse(userId , RecipientType.USER);
    }

    @Transactional
    public void markReadNotification(Long id, Long userId) {
        if(id == null || userId == null ){
            throw  new IllegalArgumentException("notification id and user id can not be null");
        }
        Notification notification = notificationRepository.findByIdAndRecipientIdAndRecipientType(id, userId, RecipientType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if(!notification.isRead()){
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void markReadAllNotification(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null"
            );
        }
        notificationRepository.markAllAsRead(userId , RecipientType.USER , LocalDateTime.now());
    }

    @Transactional
    public void resolveNotification(Long id, Long userId) {
        if(id == null || userId == null ){
            throw  new IllegalArgumentException("notification id and user id can not be null");
        }
        Notification notification = notificationRepository.findByIdAndRecipientIdAndRecipientType(id,  userId , RecipientType.USER).orElseThrow(()->new ResourceNotFoundException("Notification not found"));

        if(!notification.isResolved()){
            notification.setResolved(true);
            notification.setResolvedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void resolveAllNotification(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null"
            );
        }
         notificationRepository.resolveAll(userId , RecipientType.USER , LocalDateTime.now());
    }
}
