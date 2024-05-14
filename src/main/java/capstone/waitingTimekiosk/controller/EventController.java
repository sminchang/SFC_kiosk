package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.ShopRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import capstone.waitingTimekiosk.service.WaitingTimeService;
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
public class EventController {

    private final KakaoApi kakaoApi;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final WaitingTimeService waitingTimeService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/event")
    public String settingPage(@CookieValue(name = "accessToken", defaultValue = "not found") String accessToken,
                              @CookieValue(name = "shopId", defaultValue = "not found") String shopId,
                              @RequestParam String menuId,
                              @RequestParam int eventQuantity,
                              Model model) {
        kakaoApi.tokenCheck(accessToken);
        Shop shop = shopRepository.findById(shopId);
        List<Category> categorys = categoryRepository.findListByShopId(shop.getId());
        List<MenuItem> menus = menuItemRepository.findListByFastMenu(shop.getId(), 5);

        //event 관련값 추가 후 변경
        MenuItem menuItem = menuItemRepository.findById(menuId);
        menuItem.setEventQuantity(menuItem.getEventQuantity()+eventQuantity); //이전 값에 누적
        menuItem.setEventTime(1);
        if(menuItem.getEventQuantity() <= 0){
            menuItem.setEventTime(0);
        }
        menuItemRepository.save(menuItem);

        //전체 메뉴 최종 대기 시간 업데이트, 이벤트 수량 설정 후 실행되야 함
        waitingTimeService.makeFinalTime(shop.getId());

        model.addAttribute("categorys", categorys);
        model.addAttribute("menus",menus);
        return "html/adminPage/eventSetting";
    }
}
