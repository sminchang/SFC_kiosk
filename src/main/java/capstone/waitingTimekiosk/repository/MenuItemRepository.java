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

    @Transactional
    public void delete(String menuId) {
        Long id = Long.parseLong(menuId);
        MenuItem menuItem = em.find(MenuItem.class, id);
        em.remove(menuItem);
    }

    public List<MenuItem> findListByShopId(Long shopId) {
        return em.createQuery("select m from MenuItem m where m.shop.id =:shopId", MenuItem.class)
                .setParameter("shopId",shopId)
                .getResultList();
    }

    public List<MenuItem> findListByShopId_category(Long shopId, String categoryName) {
        return em.createQuery("select m from MenuItem m where m.shop.id =:shopId and m.category.categoryName = :categoryName", MenuItem.class)
                .setParameter("shopId",shopId)
                .setParameter("categoryName",categoryName)
                .getResultList();
    }

}
