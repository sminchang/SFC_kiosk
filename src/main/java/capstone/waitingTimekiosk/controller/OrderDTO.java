package capstone.waitingTimekiosk.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter @Setter
public class OrderDTO {
    private Long orderId;
    private Long shopId;
    private List<CartItem> orderItems = new ArrayList<>();

    @Getter @Setter
    public static class CartItem {
        private Long menuItemId;
        private String menuName;
        private Integer quantity;
    }
}