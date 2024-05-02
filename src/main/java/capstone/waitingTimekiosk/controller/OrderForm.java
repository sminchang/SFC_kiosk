package capstone.waitingTimekiosk.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter @Setter
public class OrderForm {
    private List<OrderItem> orderItems = new ArrayList<>();

    @Getter @Setter
    public static class OrderItem {
        private Long menuItemId;
        private Integer quantity;
    }
}