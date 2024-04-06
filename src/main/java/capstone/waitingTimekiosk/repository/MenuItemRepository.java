package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuItemRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(MenuItem menuItem) {
        em.persist(menuItem);
        return menuItem.getId();
    }

    public List<MenuItem> findListByShopId(Long shopId) {
        return em.createQuery("select m from MenuItem m where m.shop.id =:shopId", MenuItem.class)
                .setParameter("shopId",shopId)
                .getResultList();
    }
}
