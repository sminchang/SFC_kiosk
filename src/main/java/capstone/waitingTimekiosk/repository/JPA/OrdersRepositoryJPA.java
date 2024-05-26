package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.Orders;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Orders> findBackOrders(Long shopId) {
        return em.createQuery("select m from Orders m where m.shop.id = :shopId and m.providedTime IS NULL ", Orders.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }

    public List<Long> findBackOrderIds(Long shopId) {
        return em.createQuery("select m.id from Orders m where m.providedTime IS NULL and m.shop.id =:shopId", Long.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }

    //backOrderIds 파싱하여 해당 order들 불러오기
    public List<Orders> findOrderListByBackOrderIds(Orders order){

        // 문자열을 파싱하여 밀린 주문 ID 리스트 생성
        List<Long> backOrderIds = Arrays.stream(order.getBackOrderIds().split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        // 밀린 주문 ID를 사용하여 해당 주문들의 정보 조회
        return em.createQuery("SELECT o FROM Orders o WHERE o.id IN :backOrderIds", Orders.class)
                .setParameter("backOrderIds", backOrderIds)
                .getResultList();
    }
}
