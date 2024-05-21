package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.MenuService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class FoodCourtController {

    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final MenuService menuService;

    @GetMapping("/foodCourt/{facilityName}")
    public String foodCourIndex(@PathVariable String facilityName,
                                OrderDTO orderDTO,
                                Model model){

        List<MenuItem> menus = menuItemRepository.findListByFacilityName(facilityName);
        List<Shop> shops = shopRepository.findListByfacilityName(facilityName);

        model.addAttribute("shops", shops);
        model.addAttribute("menus", menus);
        model.addAttribute("orderDTO", orderDTO);
        return "html/foodCourt/foodCourtIndex";
    }

    @GetMapping("/foodCourtMenu")
    public String foodCourtMenu(@RequestParam String shopId,
                                @RequestParam String facilityName,
                                HttpServletResponse response,
                                OrderDTO orderDTO,
                                Model model){
        Shop shop = shopRepository.findById(shopId);
        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);;

        //shopId를 쿠키로 전송
        menuService.setCookie(response, shopId);

        model.addAttribute("categorys", categorys);
        model.addAttribute("menus",menus);
        model.addAttribute("facilityName",facilityName);
        model.addAttribute("orderDTO", orderDTO);
        return "html/foodCourt/foodCourtMenu";
    }
}
