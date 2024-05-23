package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MenuItemRepository {

    public Long save(MenuItem menuItem);

    public void delete(Long menuId);

    public MenuItem findById(Long menuItemId);

    //select m from MenuItem m where m.shop.id = :shopId and (m.finalTime <= :time)
    public List<MenuItem> findListByFastMenu(Long shopId, int time);

    //select m from MenuItem m where m.shop.id =:shopId and m.category.categoryName = :categoryName
    public List<MenuItem> findListByCategory(Long shopId, String categoryName);

    //SELECT m FROM MenuItem m WHERE m.finalTime < 6 AND m.shop.id IN (SELECT s.id FROM Shop s WHERE s.shopName LIKE CONCAT('%', :facilityName, '%'))
    public List<MenuItem> findListByFacilityName(String facilityName);
}
