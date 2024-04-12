package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Member;
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

    @Transactional
    public void delete(String menuId) {
        Long id = Long.parseLong(menuId);
        MenuItem menuItem = em.find(MenuItem.class, id);
        em.remove(menuItem);
    }

    public MenuItem findById(String menuId) {
        Long id = Long.parseLong(menuId);
        return em.find(MenuItem.class, id);
    }

    public List<MenuItem> findListByFastMenu(Long shopId, int time) {
        return em.createQuery("select m from MenuItem m where m.shop.id = :shopId and (m.defaultTime <= :time or (m.eventTime > 0 and m.eventTime <= :time))", MenuItem.class)
                .setParameter("shopId", shopId)
                .setParameter("time", time)
                .getResultList();
    }

    public List<MenuItem> findListByCategory(Long shopId, String categoryName) {
        return em.createQuery("select m from MenuItem m where m.shop.id =:shopId and m.category.categoryName = :categoryName", MenuItem.class)
                .setParameter("shopId",shopId)
                .setParameter("categoryName",categoryName)
                .getResultList();
    }
}
