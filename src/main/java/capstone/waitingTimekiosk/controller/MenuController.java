package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Member;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.OrdersRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.service.MemberService;
import capstone.waitingTimekiosk.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class MenuController {
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrdersRepository ordersRepository;
    private final ShopRepository shopRepository;
    private final MemberService memberService;
    private final MenuService menuService;
    private final KakaoApi kakaoApi;

    @Value("${spring.servlet.multipart.location}")
    private String path;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("new/shop")
    public String newMenu(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                          @RequestParam String shopName,
                          Model model) throws JsonProcessingException {
        kakaoApi.tokenCheck(accessToken);
        Member member = memberService.findMember(accessToken);
        Shop shop = new Shop(member, shopName);
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

    @PostMapping("/new/category")
    public String newCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @RequestParam String categoryName,
                              Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        menuService.validateDuplicateCategory(shop, categoryName);
        //같은 shopId로 된 categoryName을 저장할 수 없도록 처리했음, 사용자에게 경고 메세지를 띄우는거 구현해야함

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(),categoryName);

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }

    @PostMapping("/new/menuItem")
    public String newMenuItem(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              MenuForm form,
                              Model model) throws IOException {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        Category category = categoryRepository.findCategory(shop.getId(),form.getCategoryName());
        MenuItem menuItem = new MenuItem(shop);

        //외래키 연관관계 설정
        shop.addMenuItem(menuItem);
        category.addMenuItem(menuItem);

        //파일 업로드 및 파일 경로 저장
        File file = new File(path + "/img/" + UUID.randomUUID() + ".jpg"); //seqeunce++로 대체할지 고민해보기
        form.getImage().transferTo(file);
        String imagePath = "/img/" + file.getName();

        //menuItem 등록
        menuItem.setMenuName(form.getMenuName());
        menuItem.setPrice(form.getPrice());
        menuItem.setDefaultTime(form.getDefaultTime());
        menuItem.setImagePath(imagePath);
        menuItem.setDescription(form.getDescription());
        menuItemRepository.save(menuItem);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(),category.getCategoryName());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }


    @PostMapping("/update/menuItem")
    public String updateMenuItem(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                 @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                                 @RequestParam String menuId,
                                 @RequestParam String categoryName,
                                 MenuForm form,
                                 Model model) throws IOException {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        Category category = categoryRepository.findCategory(shop.getId(),categoryName);

        //파일 재등록
        MenuItem menuItem = menuItemRepository.findById(menuId);
        if(form.getImage().getSize()!=0) {
            File savedFile = new File(path + menuItem.getImagePath());
            if (savedFile.exists())
                savedFile.delete();
            File updateFile = new File(path + "img/" + UUID.randomUUID() + ".jpg"); //seqeunce++로 대체할지 고민해보기
            form.getImage().transferTo(updateFile);
            String imagePath = "/img/" + updateFile.getName();
            menuItem.setImagePath(imagePath);
        }
        //menuItem 변경
        if(form.getPrice()!=0)
            menuItem.setPrice(form.getPrice());
        if(form.getDefaultTime()!=0)
            menuItem.setDefaultTime(form.getDefaultTime());
        if(form.getDescription()!=null)
            menuItem.setDescription(form.getDescription());
        menuItemRepository.save(menuItem);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(),category.getCategoryName());

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
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(),5);

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
        String categoryName = menuItemRepository.findById(menuId).getCategory().getCategoryName();

        menuItemRepository.delete(menuId);

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(), categoryName);

        ordersRepository.removeEmptyOrders(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());
        model.addAttribute("menus", menus);
        return "html/adminPage/menuConfig";
    }

    @GetMapping("/menuConfig/category/{categoryName}")
    public String menuConfigCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @PathVariable String categoryName,
                              Model model){
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        if("fastMenu".equals(categoryName)){
            List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);
            model.addAttribute("menus", menus);
        } else{
            List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(), categoryName);
            model.addAttribute("menus", menus);
        }

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        model.addAttribute("categorys", categorys);
        model.addAttribute("menuForm", new MenuForm());

        return "html/adminPage/menuConfig";
    }

    @GetMapping("/timeSetting/category/{categoryName}")
    public String timeSettingCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @PathVariable String categoryName,
                              Model model){
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        if("fastMenu".equals(categoryName)){
            List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);
            model.addAttribute("menus", menus);
        } else{
            List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(), categoryName);
            model.addAttribute("menus", menus);
        }

        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        model.addAttribute("categorys", categorys);

        return "html/adminPage/timeSetting";
    }

    @GetMapping("/menu/category/{categoryName}")
    public ResponseEntity<List<MenuItem>> menuCategory(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                                                       @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                                                       @PathVariable String categoryName){
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);

        if("fastMenu".equals(categoryName)){
            List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);
            return ResponseEntity.ok(menus);
        } else{
            List<MenuItem> menus = menuItemRepository.findListByCategory(shop.getId(), categoryName);
            return ResponseEntity.ok(menus);
        }
    }
}
