package ecommerce.app.Ecommerce.dtos;

import java.util.List;

public class PartialOrderDeleteDto {
    private Long orderId;
   private List<Long> orderItemIds;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<Long> getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderItemIds(List<Long> orderItemIds) {
        this.orderItemIds = orderItemIds;
    }
}
