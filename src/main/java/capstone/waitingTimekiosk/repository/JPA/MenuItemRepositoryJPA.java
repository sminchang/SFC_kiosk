package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuItemRepositoryJPA implements MenuItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(MenuItem menuItem) {
        em.persist(menuItem);
        return menuItem.getId();
    }

    @Transactional
    public void delete(Long menuId) {
        MenuItem menuItem = em.find(MenuItem.class, menuId);
        em.remove(menuItem);
    }

    public MenuItem findById(Long menuItemId) {
        return em.find(MenuItem.class, menuItemId);
    }

    public List<MenuItem> findListByFastMenu(Long shopId, int time) {
        return em.createQuery("select m from MenuItem m where m.shop.id = :shopId and (m.finalTime <= :time)", MenuItem.class)
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

    public List<MenuItem> findListByFacilityName(String facilityName){
        return em.createQuery("SELECT m FROM MenuItem m WHERE m.finalTime < 6 AND m.shop.id IN (SELECT s.id FROM Shop s WHERE s.shopName LIKE CONCAT('%', :facilityName, '%'))", MenuItem.class)
                .setParameter("facilityName",facilityName)
                .getResultList();
    }

}
