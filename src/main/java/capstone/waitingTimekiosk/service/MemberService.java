package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.controller.AuthController;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MemberRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final KakaoApi kakaoApi;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final MenuItemRepository menuItemRepository;
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

}
