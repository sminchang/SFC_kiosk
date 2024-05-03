package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    public Map<String, Map<String, Integer>> calculateDemand(List<Orders> orderList) {
        Map<String, Map<String, Integer>> demandData = new HashMap<>();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Orders order : orderList) {
            LocalDateTime orderDate = order.getDate();

            for (OrderItem orderItem : order.getOrderItems()) {
                String menuName = orderItem.getMenuItem().getMenuName();
                int quantity = orderItem.getQuantity();

                if (!demandData.containsKey(menuName)) {
                    demandData.put(menuName, new HashMap<>());
                }

                Map<String, Integer> menuDemand = demandData.get(menuName);

                // 일간 수요량 계산
                if (orderDate.toLocalDate().isEqual(currentDate.toLocalDate())) {
                    menuDemand.put("daily", menuDemand.getOrDefault("daily", 0) + quantity);
                }

                // 주간 수요량 계산
                if (orderDate.isAfter(currentDate.minusWeeks(1))) {
                    menuDemand.put("weekly", menuDemand.getOrDefault("weekly", 0) + quantity);
                }

                // 월간 수요량 계산
                if (orderDate.getMonthValue() == currentDate.getMonthValue() && orderDate.getYear() == currentDate.getYear()) {
                    menuDemand.put("monthly", menuDemand.getOrDefault("monthly", 0) + quantity);
                }

                // 연간 수요량 계산
                if (orderDate.getYear() == currentDate.getYear()) {
                    menuDemand.put("annual", menuDemand.getOrDefault("annual", 0) + quantity);
                }
            }
        }

        return demandData;
    }
}
