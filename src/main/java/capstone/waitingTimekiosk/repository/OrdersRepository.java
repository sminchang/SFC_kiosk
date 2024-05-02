package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Orders orders) {
        em.persist(orders);
        return orders.getId();
    }

    public List<Orders> findListByShopId(Long shopId) {
        return em.createQuery("select m from Orders m where m.shop.id = :shopId and m.status = false", Orders.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }
}
