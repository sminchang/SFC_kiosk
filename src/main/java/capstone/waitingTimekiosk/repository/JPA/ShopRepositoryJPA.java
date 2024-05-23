package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShopRepositoryJPA implements ShopRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Shop shop) {
        em.persist(shop);
        return shop.getId();
    }

    @Transactional
    public void delete(Long shopId) {
        Shop shop = em.find(Shop.class, shopId);
        em.remove(shop);
    }

    public List<Shop> findListByMemberId(Long id) {
        return em.createQuery("select m from Shop m where m.member.id = :id", Shop.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Shop findById(Long shopId) {
        return em.find(Shop.class, shopId);
    }

    public List<Shop> findListByfacilityName(String facilityName){
        return em.createQuery("SELECT s FROM Shop s WHERE s.shopName LIKE CONCAT('%', :facilityName, '%')", Shop.class)
                .setParameter("facilityName", facilityName)
                .getResultList();
    }
}
