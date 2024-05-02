package capstone.waitingTimekiosk.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;


@RequiredArgsConstructor
@Controller
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("new/order")
    public String orderFromCart(@ModelAttribute OrderForm orderForm){
        List<OrderForm.OrderItem> orderItems = orderForm.getOrderItems();

        logger.info("orderItems size: {}", orderItems.size());
        if (orderItems != null) {
            for (OrderForm.OrderItem orderItem : orderItems) {
                logger.info("Id: {}, Quantity: {}", orderItem.getMenuItemId(), orderItem.getQuantity());
            }
        } else {
            logger.info("orderItems is null");
        }

        return "redirect:/menu";
    }
}
