package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.domain.Orders;
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
}
