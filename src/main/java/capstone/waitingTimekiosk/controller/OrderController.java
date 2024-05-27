package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.*;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.OrderItemRepository;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.service.WaitingTimeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Controller
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    private final WaitingTimeService waitingTimeService;

    @PostMapping("new/order")
    public String orderFromCart(/*@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                */@CookieValue(name = "shopId", defaultValue = "not found") Long shopId,
                                  @RequestParam(required = false) String facilityName,
                                  @ModelAttribute OrderDTO orderDTO,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request){

        //걸제 기능이 도입되면 해당 기능 없어도 문제 없을 것으로 생각
        //kakaoApi.tokenCheck(accessToken); //foodCourtMenu에서 이용 시 인가가 안되는 문제 때문에 삭제, 추후 문제가 되면 수정

        Shop shop = shopRepository.findById(shopId);

        //주문 항목 생성
        Orders order = new Orders(shop);
        //주문 시점에 밀린 주문들 기입--추후 예상 대기시간 알고리즘 모델링 시 소요 시간에 분석에 반영
        List<Long> backOrders = ordersRepository.findBackOrderIds(shop.getId());
        order.setBackOrderIds(
                backOrders.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))
        );
        ordersRepository.save(order);

        //주문 항목 내부 주문 내역 생성
        List<OrderDTO.CartItem> cartItems = orderDTO.getOrderItems();
        for (OrderDTO.CartItem cartItem : cartItems){
            if (cartItem.getMenuItemId() != null) {
            logger.info("Id: {}, Quantity: {}", cartItem.getMenuItemId(), cartItem.getQuantity());
            OrderItem orderItem = new OrderItem(order);
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId());
            orderItem.setOrders(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);

            //이벤트 수량 항목일 경우 수량 감소, 이벤트 수량이 끝난 경우 대기 시간 초기화
            int eventQuantity = menuItem.getEventQuantity() - cartItem.getQuantity();
            if(eventQuantity > 0) {
                menuItem.setEventQuantity(eventQuantity);
            }
            else if(eventQuantity <= 0){ //이벤트 수량 초과시 기본 시간 적용
                menuItem.setEventQuantity(0);
                menuItem.setEventTime(0);
            }
            menuItemRepository.save(menuItem);

            //전체 메뉴 최종 대기 시간 업데이트
            waitingTimeService.makeFinalTime(shop.getId());

            //외래키 연관관계 설정
            order.addOrderItem(orderItem);
            menuItem.addOrderItem(orderItem);
        }
        }

        //직전 요청이 foodCourtMenu였다면 foodCourtMenu로 리다이렉트
        if (request.getHeader("Referer").contains("/foodCourtMenu")) {
            redirectAttributes.addAttribute("shopId", shopId);
            redirectAttributes.addAttribute("facilityName", facilityName);
            return "redirect:/foodCourtMenu";
        }

        return "redirect:/menu";
    }

    @PostMapping("orderComplete")
    public String orderComplete(@RequestParam Long orderId){

        Orders order = ordersRepository.findById(orderId);
        order.setProvidedTime(LocalDateTime.now());
        ordersRepository.save(order);

        //전체 메뉴 최종 대기 시간 업데이트
        waitingTimeService.makeFinalTime(order.getShop().getId());

        return "redirect:/orderState";
    }

}
