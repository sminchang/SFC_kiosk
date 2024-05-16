package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Shop;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShopRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Shop shop) {
        em.persist(shop);
        return shop.getId();
    }

    @Transactional
    public void delete(String shopId) {
        Long id = Long.parseLong(shopId);
        Shop shop = em.find(Shop.class, id);
        em.remove(shop);
    }

    public List<Shop> findListByMemberId(Long id) {
        return em.createQuery("select m from Shop m where m.member.id = :id", Shop.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Shop findById(String shopId) {
        Long id = Long.parseLong(shopId);
        return em.find(Shop.class, id);
    }

    public List<Shop> findListByfacilityName(String facilityName){
        return em.createQuery("SELECT s FROM Shop s WHERE s.shopName LIKE CONCAT('%', :facilityName, '%')", Shop.class)
                .setParameter("facilityName", facilityName)
                .getResultList();
    }
}
