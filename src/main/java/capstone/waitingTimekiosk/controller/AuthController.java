package capstone.waitingTimekiosk.controller;


import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.service.MemberService;
import capstone.waitingTimekiosk.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor //생성자 자동 생성, 의존관계 자동 주입
@Controller
public class AuthController {

    private final KakaoApi kakaoApi;
    private final ShopRepository shopRepository;
    private final MemberService memberService;
    private final MenuItemRepository menuItemRepository;
    private final MenuService menuService;
    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    //초기화면, 카카오 API 설정이 바뀌면 yml만 수정하도록 설계
    //페이지 소스를 보면 앱키가 그대로 노출되는 취약점이 있다.(로그아웃도 마찬가지)
    @GetMapping("/")
    public String startForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoApi.getKakaoRedirectUri());
        return "index";
    }

    @RequestMapping("/memberIndex")
    public String login(HttpServletResponse response, @RequestParam String code, Model model) throws JsonProcessingException {
        //1. 인가 코드 받기 - @RequestParam String code

        //2. 토큰 받기
        String accessToken = kakaoApi.getAccessToken(code);

        //3.사용자 정보 받기
        Member member = kakaoApi.getUserInfo(accessToken);

        //3-1.사용자 중복검사
        Long memberId = memberService.validateDuplicateMember(member);

        //4.OAuth 서버로부터 받은 액세스 토큰을 응답 쿠키 헤더에 넣어 클라이언트 브라우저에 보내기
        kakaoApi.setCookie(response, accessToken);

        //회원의 shop 조회
        List<Shop> shops;
        try {
            shops = shopRepository.findListByMemberId(memberId);
            model.addAttribute("shops",shops);
        } catch (Exception e){
            logger.info("회원이 등록한 shop이 없습니다.");
        }

        //서버 사이드 렌더링
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());

        return "memberIndex";
    }

    //클라이언트가 로그아웃 요청 시 카카오 계정 로그아웃 후 리다이렉트 되어 액세스 토큰 만료 요청 컨트롤러로 이동
    @RequestMapping("/logout")
    public String deleteToken(@CookieValue(name = "accessToken", defaultValue = "not found") Cookie tokenCookie,
                            HttpServletResponse response) throws JsonProcessingException {

        //카카오 서버에 액세스 토큰 만료 요청
        String accessToken = tokenCookie.getValue();
        kakaoApi.serviceLogout(accessToken);
        logger.info("logout-accessToken={}",accessToken);

        //브라우저 액세스 토큰 쿠키 초기화
        tokenCookie.setValue(null);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);

        //로그: 쿠키 초기화 여부 확인
        String initToken = tokenCookie.getValue();
        logger.info("init-accessToken={}", initToken);

        return "redirect:/";
    }

    @GetMapping("/memberMenu")
    public String memberPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                             HttpServletResponse response,
                             @RequestParam String shopId,
                             Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);

        //클라이언트가 앞으로의 페이지에서 shopId를 기억하도록 쿠키 전송
        menuService.setCookie(response, shopId);

        model.addAttribute("nickname",member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());
        return "memberMenu";
    }

    //홈화면으로 돌아가는 경우
    @GetMapping("/backHome")
    public String homePage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                           Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);

        //회원의 shop 조회
        List<Shop> shops;
        try {
            shops = shopRepository.findListByMemberId(member.getId());
            model.addAttribute("shops",shops);
        } catch (Exception e){
            logger.info("회원이 등록한 shop이 없습니다.");
        }

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());
        return "memberIndex";
    }

    @GetMapping("/menuConfig")
    public String ConfigPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                             @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                             Model model){
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);

        model.addAttribute("categorys", categorys);
        model.addAttribute("menus",menus);
        model.addAttribute("menuForm", new MenuForm());
        return "html/adminPage/menuConfig";
    }

    @GetMapping("/menuDemand")
    public String demandPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken) {
        kakaoApi.tokenCheck(accessToken);
        return "html/adminPage/menuDemand";
    }

    @GetMapping("/orderState")
    public String statePage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken) {
        kakaoApi.tokenCheck(accessToken);
        return "html/adminPage/orderState";
    }

    @GetMapping("/timeSetting")
    public String settingPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);;

        model.addAttribute("categorys", categorys);
        model.addAttribute("menus",menus);
        return "html/adminPage/timeSetting";
    }

    @GetMapping("/menu")
    public String menuPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                           @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                           Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);;

        model.addAttribute("categorys", categorys);
        model.addAttribute("menus",menus);
        return "html/consumerPage/menu";
    }
}
