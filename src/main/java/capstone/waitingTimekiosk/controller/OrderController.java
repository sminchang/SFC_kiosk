package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.domain.Orders;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.OrderItemRepository;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final KakaoApi kakaoApi;
    private final ShopRepository shopRepository;

    @PostMapping("new/order")
    public String orderFromCart(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                                @ModelAttribute OrderForm orderForm){
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        //주문 항목 생성
        Orders orders = new Orders(shop);
        ordersRepository.save(orders);

        //주문 항목 내부 주문 내역 생성
        List<OrderForm.CartItem> cartItems = orderForm.getOrderItems();
        for (OrderForm.CartItem cartItem : cartItems){
            logger.info("Id: {}, Quantity: {}", cartItem.getMenuItemId(), cartItem.getQuantity());
            OrderItem orderItem = new OrderItem(orders);
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId());
            orderItem.setOrders(orders);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);

            //외래키 연관관계 설정
            orders.addOrderItem(orderItem);
            menuItem.addOrderItem(orderItem);
        }

        return "redirect:/menu";
    }

    @PostMapping("orderComplete")
    public String orderComplete(@RequestParam Long orderId){

        Orders order = ordersRepository.findById(orderId);
        order.setStatus(true);
        ordersRepository.save(order);

        return "redirect:/orderState";
    }

}
