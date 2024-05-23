package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

public interface MemberRepository {


    public Long save(Member member);

    //SELECT m FROM Member m WHERE m.email = :email
    public Member findByEmail(String email);

    public Member findById(Long id);
}