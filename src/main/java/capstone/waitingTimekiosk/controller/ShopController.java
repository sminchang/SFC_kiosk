package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ShopController {

    private final KakaoApi kakaoApi;
    private final MemberService memberService;
    private final ShopRepository shopRepository;

    @PostMapping("new/shop")
    public String newShop(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                          @RequestParam String shopName,
                          @RequestParam float CCR,
                          Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);
        Shop shop = new Shop(member, shopName);
        shop.setCCR(CCR);
        shopRepository.save(shop);

        //외래키 연관관계 설정
        member.addShop(shop);

        List<Shop> shops = shopRepository.findListByMemberId(member.getId());
        model.addAttribute("shops",shops);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());

        return "memberIndex";
    }

    @GetMapping("/remove/shop")
    public String removeShop(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                             @RequestParam String shopId,
                             Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);

        shopRepository.delete(shopId);

        List<Shop> shops = shopRepository.findListByMemberId(member.getId());
        model.addAttribute("shops",shops);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());
        return "memberIndex";
    }
}
