package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.controller.AuthController;
import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final KakaoApi kakaoApi;
    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    //email 중복 회원 검증
    public Long validateDuplicateMember(Member member) {
        try{
            return memberRepository.findByEmail(member.getEmail()).getId();
        } catch (Exception e) {
            return memberRepository.save(member);}
    }

    //accessToken으로 회원 정보조회
    public Member findMember(String accessToken) throws JsonProcessingException {
        Member member = kakaoApi.getUserInfo(accessToken);
        return memberRepository.findByEmail(member.getEmail());
    }

    //같은 shopId를 사용하면서 같은 카테고리명을 사용할 수 없도록 예외처리
    public Long validateDuplicateCategory(Shop shop, String categoryName){
        try {
            return categoryRepository.findCategory(shop.getId(), categoryName).getId();
        }catch (EmptyResultDataAccessException e){
            Category category = new Category(shop, categoryName);
            return categoryRepository.save(category);
        }
    }

}
