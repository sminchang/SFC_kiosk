package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Category category) {
        em.persist(category);
        return category.getId();
    }

    public List<Category> findAll(Long shopId) {
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
