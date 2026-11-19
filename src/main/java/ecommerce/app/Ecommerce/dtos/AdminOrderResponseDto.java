package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminOrderResponseDto(

        Long id,
        Long userId,
        String userName,
        String userEmail,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        LocalDateTime createdAt

        ) {}