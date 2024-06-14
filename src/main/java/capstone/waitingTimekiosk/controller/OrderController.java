package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.*;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import capstone.waitingTimekiosk.service.WaitingTimeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Controller
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrdersRepository ordersRepository;
    private final WaitingTimeService waitingTimeService;

    @GetMapping("new/order")
    public String orderFromCart(/*@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                */@CookieValue(name = "shopId", defaultValue = "not found") Long shopId,
                                  @RequestParam(required = false) String facilityName,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) throws Exception {

        /*걸제 기능이 도입되면 해당 기능 없어도 문제 없을 것으로 생각
        kakaoApi.tokenCheck(accessToken); //foodCourtMenu에서 이용 시 인가가 안되는 문제 때문에 삭제, 추후 문제가 되면 수정*/

        /*주문 생성 로직을 OrderWebSocketHandler에게 위임함,
        이유인즉슨 orderId 생성 후 orderstate.html에 웹소켓 데이터를 응답해야하는 문제 때문*/

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
