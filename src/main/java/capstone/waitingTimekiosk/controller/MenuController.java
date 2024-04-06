package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.MemberService;
import capstone.waitingTimekiosk.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("new/shop")
    public String newMenu(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                          @RequestParam String shopName,
                          Model model) throws JsonProcessingException {

        Member member = memberService.findMember(accessToken);
        Shop shop = new Shop(member, shopName);
        shopRepository.save(shop);

        return "redirect:/"; //로그인 직후 화면에서 렌더링하는 일이라 로그인과 이어져야해서 초기화면으로 넘기게됨, 추후 해결방안 모색하기
    }

    @PostMapping("/new/category")
    public String newCategory(@CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @RequestParam String categoryName, Model model) throws JsonProcessingException {

        Shop shop = shopRepository.findById(shopId);

        menuService.validateDuplicateCategory(shop, categoryName);
        //같은 shopId로 된 categoryName을 저장할 수 없도록 처리했음, 사용자에게 경고 메세지를 띄우는거 구현해야함

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        return "html/adminPage/menuConfig";
    }

    @PostMapping("/new/menuItem")
    public String newMenuItem(@CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              MenuForm form,
                              Model model) throws JsonProcessingException {

        Shop shop = shopRepository.findById(shopId);
        Category category = categoryRepository.findCategory(shop.getId(),form.getCategoryName());

        MenuItem menuItem = new MenuItem(shop);
        menuItem.setMenuName(form.getMenuName());
        menuItem.setCategory(category);
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
}
