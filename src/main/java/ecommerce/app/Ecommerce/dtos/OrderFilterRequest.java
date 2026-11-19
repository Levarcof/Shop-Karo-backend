package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderFilterRequest {

    private OrderStatus status;
    private Long userId;

    @Size(max = 100, message = "Search cannot exceed 100 characters")
    private String search;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Min(value = 0, message = "Page cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 20;

    private String sortBy = "createdAt";

    private String direction = "DESC";








    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public @Size(max = 100, message = "Search cannot exceed 100 characters") String getSearch() {
        return search;
    }

    public void setSearch(@Size(max = 100, message = "Search cannot exceed 100 characters") String search) {
        this.search = search;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Min(value = 0, message = "Page cannot be negative")
    public int getPage() {
        return page;
    }

    public void setPage(@Min(value = 0, message = "Page cannot be negative") int page) {
        this.page = page;
    }

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    public int getSize() {
        return size;
    }

    public void setSize(@Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
