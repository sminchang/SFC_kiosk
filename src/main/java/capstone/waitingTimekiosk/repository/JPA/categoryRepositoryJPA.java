package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class categoryRepositoryJPA implements CategoryRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Category category) {
        em.persist(category);
        return category.getId();
    }

    @Transactional
    public void delete(String categoryId) {
        Long id = Long.parseLong(categoryId);
        Category category = em.find(Category.class, id);
        em.remove(category);
    }

    public List<Category> findListByShopId(Long shopId) {
        return em.createQuery("select m from Category m where m.shop.id =:shopId", Category.class)
                .setParameter("shopId",shopId)
                .getResultList();
    }

    public Category findCategory(Long shopId, String categoryName) {
        return em.createQuery("select m from Category m where m.shop.id = :shopId and m.categoryName = :categoryName", Category.class)
                .setParameter("shopId",shopId)
                .setParameter("categoryName",categoryName)
                .getSingleResult();
    }
}
