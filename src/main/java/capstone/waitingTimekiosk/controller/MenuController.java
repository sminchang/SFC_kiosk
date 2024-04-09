package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.service.MemberService;
import capstone.waitingTimekiosk.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MenuController {
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    private final MemberService memberService;
    private final MenuService menuService;
    private final KakaoApi kakaoApi;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("new/shop")
    public String newMenu(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                          @RequestParam String shopName,
                          Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);
        Shop shop = new Shop(member, shopName);
        shopRepository.save(shop);

        List<Shop> shops = shopRepository.findListByMemberId(member.getId());
        model.addAttribute("shops",shops);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("logoutRedirectUri", kakaoApi.getKakaoLogoutRedirectUri());

        return "memberIndex";
    }

    @PostMapping("/new/category")
    public String newCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @RequestParam String categoryName, Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        menuService.validateDuplicateCategory(shop, categoryName);
        //같은 shopId로 된 categoryName을 저장할 수 없도록 처리했음, 사용자에게 경고 메세지를 띄우는거 구현해야함

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByShopId(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }

    @PostMapping("/new/menuItem")
    public String newMenuItem(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              MenuForm form,
                              Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        Category category = categoryRepository.findCategory(shop.getId(),form.getCategoryName());
        MenuItem menuItem = new MenuItem(shop);

        //외래키 연관관계 설정
        shop.addMenuItem(menuItem);
        category.addMenuItem(menuItem);

        //menuItem 등록
        menuItem.setMenuName(form.getMenuName());
        menuItem.setPrice(form.getPrice());
        menuItem.setDefaultTime(form.getDefaultTime());
        menuItem.setImagePath(form.getImagePath());
        menuItem.setDescription(form.getDescription());
        menuItemRepository.save(menuItem);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByShopId(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
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

    @GetMapping("/remove/category")
    public String removeCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                 @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                                 @RequestParam String categoryId,
                                 Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        categoryRepository.delete(categoryId);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByShopId(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }

    @GetMapping("/remove/menuItem")
    public String removeMenuItem(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                 @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                                 @RequestParam String menuId,
                                 Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        menuItemRepository.delete(menuId);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByShopId(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }
}
