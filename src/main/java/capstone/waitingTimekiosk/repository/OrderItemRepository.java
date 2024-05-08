package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;


@Repository
public class OrderItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(OrderItem orderItem) {
        em.persist(orderItem);
        return orderItem.getId();
    }
}
