package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.controller.OrderDTO;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.domain.Orders;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.OrderItemRepository;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final WaitingTimeService waitingTimeService;
    private final ShopRepository shopRepository;

    @Transactional
    public Orders createOrder(Long shopId, OrderDTO orderDTO) {
        Shop shop = shopRepository.findById(shopId);

        // 주문 항목 생성
        Orders order = new Orders(shop);
        List<Long> backOrders = ordersRepository.findBackOrderIds(shop.getId());
        order.setBackOrderIds(
                backOrders.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
        );
        ordersRepository.save(order);

        // 주문 항목 내부 주문 내역 생성
        List<OrderDTO.CartItem> cartItems = orderDTO.getOrderItems();
        for (OrderDTO.CartItem cartItem : cartItems) {
            if (cartItem.getMenuItemId() != null) {
                OrderItem orderItem = new OrderItem(order);
                MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId());
                orderItem.setOrders(order);
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItemRepository.save(orderItem);

                // 이벤트 수량 항목일 경우 수량 감소, 이벤트 수량이 끝난 경우 대기 시간 초기화
                int eventQuantity = menuItem.getEventQuantity() - cartItem.getQuantity();
                if (eventQuantity > 0) {
                    menuItem.setEventQuantity(eventQuantity);
                } else if (eventQuantity <= 0) {
                    menuItem.setEventQuantity(0);
                    menuItem.setEventTime(0);
                }
                menuItemRepository.save(menuItem);

                // 외래키 연관관계 설정
                order.addOrderItem(orderItem);
                menuItem.addOrderItem(orderItem);
            }
        }

        // 전체 메뉴 최종 대기 시간 업데이트
        waitingTimeService.makeFinalTime(shop.getId());

        return order;
    }
}