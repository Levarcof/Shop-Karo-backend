package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Notification;
import ecommerce.app.Ecommerce.enums.NotificationType;
import ecommerce.app.Ecommerce.enums.RecipientType;
import ecommerce.app.Ecommerce.enums.ReferenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository  extends JpaRepository<Notification , Long> {

    Optional<Notification> findByReferenceTypeAndReferenceIdAndTypeAndIsResolvedFalse(
            ReferenceType referenceType,
            Long referenceId,
            NotificationType type
    );

    Page<Notification> findByRecipientIdAndRecipientType(Long userId, RecipientType recipientType, Pageable pageable);

    Long countByRecipientIdAndRecipientTypeAndIsReadFalse(Long userId, RecipientType recipientType);


    Optional<Notification> findByIdAndRecipientIdAndRecipientType(Long id, Long userId, RecipientType recipientType);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true,
            n.readAt = :now
        WHERE n.recipientId = :userId
        AND n.recipientType = :recipientType
        AND n.isRead = false
    """)
    int markAllAsRead(
            @Param("userId") Long userId,
            @Param("recipientType") RecipientType recipientType,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isResolved = true,
            n.resolvedAt = :now
        WHERE n.recipientId = :userId
        AND n.recipientType = :recipientType
        AND n.isResolved = false
    """)
    int resolveAll(
            @Param("userId") Long userId,
            @Param("recipientType") RecipientType recipientType,
            @Param("now") LocalDateTime now
    );

}
