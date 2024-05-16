package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class FoodCourtController {

    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;

    @GetMapping("/foodCourt/{facilityName}")
    public String foodCourtMenu(@PathVariable String facilityName,
                                OrderForm orderForm,
                                Model model){

        List<MenuItem> menus = menuItemRepository.findListByFacilityName(facilityName);
        List<Shop> shops = shopRepository.findListByfacilityName(facilityName);

        model.addAttribute("shops", shops);
        model.addAttribute("menus", menus);
        model.addAttribute("orderForm", orderForm);
        return "foodCourt";
    }
}
