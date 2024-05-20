package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DemandService {
    public Map<Long, Map<String, Object>> calculateDemand(List<Orders> orderList) {
        Map<Long, Map<String, Object>> demandData = new HashMap<>();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Orders order : orderList) {
            LocalDateTime orderDate = order.getOrderTime();

            for (OrderItem orderItem : order.getOrderItems()) {
                Long menuItemId = orderItem.getMenuItem().getId();
                String menuName = orderItem.getMenuItem().getMenuName();
                int quantity = orderItem.getQuantity();

                if (!demandData.containsKey(menuItemId)) {
                    Map<String, Object> menuData = new HashMap<>();
                    menuData.put("menuName", menuName);
                    menuData.put("daily", 0);
                    menuData.put("weekly", 0);
                    menuData.put("monthly", 0);
                    menuData.put("annual", 0);
                    demandData.put(menuItemId, menuData);
                }

                Map<String, Object> menuData = demandData.get(menuItemId);

                // 일간 수요량 계산
                if (orderDate.toLocalDate().isEqual(currentDate.toLocalDate())) {
                    menuData.put("daily", (int) menuData.get("daily") + quantity);
                }

                // 주간 수요량 계산
                if (orderDate.isAfter(currentDate.minusWeeks(1))) {
                    menuData.put("weekly", (int) menuData.get("weekly") + quantity);
                }

                // 월간 수요량 계산
                if (orderDate.getMonthValue() == currentDate.getMonthValue() && orderDate.getYear() == currentDate.getYear()) {
                    menuData.put("monthly", (int) menuData.get("monthly") + quantity);
                }

                // 연간 수요량 계산
                if (orderDate.getYear() == currentDate.getYear()) {
                    menuData.put("annual", (int) menuData.get("annual") + quantity);
                }
            }
        }

        //annual 기준으로 내림차순 정렬, 페이지에서 주문량이 많은 정보가 상단에 위치하게 함
        Map<Long, Map<String, Object>> sortedDemandData = new LinkedHashMap<>();
        demandData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue((v1, v2) -> Integer.compare((int) v2.get("annual"), (int) v1.get("annual"))))
                .forEachOrdered(entry -> sortedDemandData.put(entry.getKey(), entry.getValue()));

        return sortedDemandData;
    }
}
