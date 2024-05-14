package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class OrderItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(OrderItem orderItem) {
        em.persist(orderItem);
        return orderItem.getId();
    }

    public List<OrderItem> findListByOrderId(Long orderId) {
        return em.createQuery("select m from OrderItem m where m.orders.id = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }


    //orderStatus 반영하여 waitingTime에 필요한 수량 조회
    public List<Object[]> findWaitingTimeListByShopId(Long shopId) {
        return em.createQuery("SELECT m.menuItem, SUM(m.quantity) " +
                        "FROM OrderItem m " +
                        "WHERE m.orders.shop.id = :shopId AND m.orders.status = false " +
                        "GROUP BY m.menuItem", Object[].class)
                .setParameter("shopId", shopId)
                .getResultList();
    }

    //현재 주문 대기에 들어가지 않은 메뉴들의 경우 finalTime 초기화
    @Transactional
    public void initFinalTime(Long shopId) {
        em.createQuery("UPDATE MenuItem m " +
                        "SET m.finalTime = CASE WHEN m.eventTime > 0 THEN m.eventTime ELSE m.defaultTime END " +
                        "WHERE m.shop.id = :shopId " +
                        "AND m.id NOT IN (" +
                        "    SELECT oi.menuItem.id " +
                        "    FROM OrderItem oi " +
                        "    WHERE oi.orders.status = false " +
                        ")")
                .setParameter("shopId", shopId)
                .executeUpdate();
    }
}
