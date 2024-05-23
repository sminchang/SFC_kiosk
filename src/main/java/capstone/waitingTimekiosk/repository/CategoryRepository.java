package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CategoryRepository {

    public Long save(Category category);

    public void delete(String categoryId);

    //select m from Category m where m.shop.id =:shopId
    public List<Category> findListByShopId(Long shopId);

    //select m from Category m where m.shop.id = :shopId and m.categoryName = :categoryName
    public Category findCategory(Long shopId, String categoryName);

}
