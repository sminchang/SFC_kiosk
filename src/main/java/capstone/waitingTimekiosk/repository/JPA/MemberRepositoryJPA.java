package capstone.waitingTimekiosk.repository.JPA;

import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryJPA implements MemberRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findByEmail(String email) {
        return em.createQuery("SELECT m FROM Member m WHERE m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }
}
