package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.Orders;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersRepositoryJPA implements OrdersRepository {


    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Orders orders) {
        em.persist(orders);
        return orders.getId();
    }

    //orderItem이 모두 삭제된 빈 orders를 삭제
    @Transactional
    public void deleteEmptyOrders(Long shopId) {
        List<Orders> emptyOrders = em.createQuery("select o from Orders o where o.shop.id = :shopId and o.orderItems is empty", Orders.class)
                .setParameter("shopId", shopId)
                .getResultList();

        for (Orders order : emptyOrders) {
            em.remove(order);
        }
    }

    public Orders findById(Long orderId) {
        return em.find(Orders.class, orderId);
    }

    public List<Orders> findListByShopId(Long shopId) {
        return em.createQuery("select m from Orders m where m.shop.id = :shopId", Orders.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }

    public List<Orders> findListByShopIdAndFalse(Long shopId) {
        return em.createQuery("select m from Orders m where m.shop.id = :shopId and m.status = false", Orders.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }
}