package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Shop;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ShopRepository {

    public Long save(Shop shop);

    public void delete(Long shopId);

    //select m from Shop m where m.member.id = :id
    public List<Shop> findListByMemberId(Long id);

    public Shop findById(Long shopId);

    //SELECT s FROM Shop s WHERE s.shopName LIKE CONCAT('%', :facilityName, '%')
    public List<Shop> findListByfacilityName(String facilityName);
}
