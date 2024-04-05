package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.MenuItem;
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

    public List<Shop> findListByMemberId(Long id) {
        return em.createQuery("select m from Shop m where m.member.id = :id", Shop.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Shop findByMemberId(Long id) {
        return em.createQuery("select m from Shop m where m.member.id = :id", Shop.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
