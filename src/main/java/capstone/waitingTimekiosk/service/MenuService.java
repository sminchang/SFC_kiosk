package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.domain.Category;
import capstone.waitingTimekiosk.domain.Shop;
import capstone.waitingTimekiosk.repository.CategoryRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final CategoryRepository categoryRepository;

    //같은 shopId를 사용하면서 같은 카테고리명을 사용할 수 없도록 예외처리
    public Long validateDuplicateCategory(Shop shop, String categoryName){
        try {
            return categoryRepository.findCategory(shop.getId(), categoryName).getId();
        }catch (EmptyResultDataAccessException e){
            Category category = new Category(shop, categoryName);
            //외래키 연관관계 설정
            shop.addCategory(category);
            return categoryRepository.save(category);
        }
    }

    public void setCookie(HttpServletResponse response, String shopId) {
        Cookie cookie = new Cookie("shopId", shopId);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); //https 세팅시 사용
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
