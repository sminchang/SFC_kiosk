package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface OrderItemRepository {


    public Long save(OrderItem orderItem);

    //select m from OrderItem m where m.orders.id = :orderId
    public List<OrderItem> findListByOrderId(Long orderId);


    //orderStatus 반영하여 waitingTime에 필요한 수량 조회
    //SELECT m.menuItem, SUM(m.quantity) FROM OrderItem m WHERE m.orders.shop.id = :shopId AND m.orders.status = false GROUP BY m.menuItem
    public List<Object[]> findWaitingTimeListByShopId(Long shopId);

    //현재 주문 대기에 들어가지 않은 메뉴들의 경우 finalTime 초기화
    /*UPDATE MenuItem m " +
                        "SET m.finalTime = CASE WHEN m.eventTime > 0 THEN m.eventTime ELSE m.defaultTime END " +
                        "WHERE m.shop.id = :shopId " +
                        "AND m.id NOT IN (" +
                        "    SELECT oi.menuItem.id " +
                        "    FROM OrderItem oi " +
                        "    WHERE oi.orders.status = false " +
                        ")*/
    //@Transactional
    public void initFinalTime(Long shopId);
}
