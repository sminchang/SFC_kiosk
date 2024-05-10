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

    //제공 대기 중인 메뉴별 수량
    public List<Object[]> findAccumulatedQuantities(Long shopId) {
        return em.createQuery("select m.menuItem.id, sum(m.quantity) " +
                        "from OrderItem m " +
                        "where m.orders.shop.id = :shopId and m.orders.status = false " +
                        "group by m.menuItem.id", Object[].class)
                .setParameter("shopId", shopId)
                .getResultList();
    }
}
