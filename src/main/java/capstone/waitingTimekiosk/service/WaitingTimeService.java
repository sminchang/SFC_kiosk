package capstone.waitingTimekiosk.service;

import capstone.waitingTimekiosk.controller.AuthController;
import capstone.waitingTimekiosk.domain.MenuItem;
import capstone.waitingTimekiosk.domain.OrderItem;
import capstone.waitingTimekiosk.repository.MenuItemRepository;
import capstone.waitingTimekiosk.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/*
    긴 조리 시간이 의미하는 것이 조리사가 직접 붙들고 있는 시간이라기 보다는 익히는데 걸리는 시간이 길다는 의미로 해석했다.
    때문에 조리기(튀김기,오븐,찜기..)에서 메뉴를 가열하는 동안 조리사는 다른 일을 할 수 있다.
    그래서 조리 시간이 가장 긴 메뉴를 기준으로 주문 전체의 대기 시간을 부여해도 그 시간 안에 모든 메뉴가 나올 수 있다고 보았다.
*/

@Service
@RequiredArgsConstructor
public class WaitingTimeService {

    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final Logger logger = LoggerFactory.getLogger(WaitingTimeService.class);

    //개별 메뉴 단위 예상 대기 시간
    public void makeFinalTime(Long shopId) {
        List<Object[]> items = orderItemRepository.findWaitingTimeListByShopId(shopId);
        orderItemRepository.initFinalTime(shopId);

        for (Object[] item : items) {
            MenuItem menuItem = (MenuItem) item[0];
            int quantity = ((Long) item[1]).intValue();
            logger.info("Name:{}, quantity:{}",menuItem.getMenuName(), quantity);
            int waitTime;
            int finalTime = 1;
            int rest;

            //이벤트 타임이 설정된 메뉴일 경우 남은 이벤트 수량보다 많은 수량이 주문되었는지 확인 후 추가 대기시간 설정
            if(menuItem.getEventTime() > 0){
                waitTime = menuItem.getEventTime();
                if(menuItem.getEventQuantity() - quantity < 0){
                    rest = Math.abs(quantity - menuItem.getEventQuantity());
                    if(rest / menuItem.getCCQ() > 0){
                        if(rest % menuItem.getCCQ() != 0){
                            finalTime =(rest / menuItem.getCCQ() + 1) * waitTime;
                        } else{
                            finalTime = rest / menuItem.getCCQ() * waitTime;
                        }
                    }
                } else{
                    finalTime = waitTime;
                }
            }
            //이벤트 타임이 설정되지 않은 메뉴의 경우 동시 조리 가능한 수량 단위로 추가 대기시간 설정
            else {
                waitTime = menuItem.getDefaultTime();
                if(quantity / menuItem.getCCQ() > 0){
                    if(quantity % menuItem.getCCQ() != 0){
                        finalTime = (quantity / menuItem.getCCQ() + 1) * waitTime;
                    } else{
                        finalTime = quantity / menuItem.getCCQ() * waitTime;
                    }
                } else{
                    finalTime = waitTime;
                }
            }

            logger.info("finalTime: {}",finalTime);
            menuItem.setFinalTime(finalTime);
            menuItemRepository.save(menuItem);
        }
    }

    //주문서 단위 예상 대기 시간
    public float makelastTime(Long orderId){
        List<OrderItem> orderItems = orderItemRepository.findListByOrderId(orderId);

        // 주문 내역 중 조리 시간이 가장 긴 메뉴 기준으로 최종 대기 시간 선정
        OrderItem longestItem = orderItems.stream()
                .max(Comparator.comparingInt(item -> item.getMenuItem().getDefaultTime()))
                .orElse(null);

        //eventTime이 셋팅된 메뉴로만 주문된 경우 eventTime 중 가장 큰 메뉴를 사용
        if (longestItem == null){
            longestItem = orderItems.stream()
                    .max(Comparator.comparingInt(item -> item.getMenuItem().getEventTime()))
                    .orElse(null);
        }

        return longestItem.getQuantity()/longestItem.getMenuItem().getCCQ()*longestItem.getMenuItem().getDefaultTime();
    }

}
