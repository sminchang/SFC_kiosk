package capstone.waitingTimekiosk.repository;

import capstone.waitingTimekiosk.domain.Orders;

import java.util.List;

public interface OrdersRepository {

    public Long save(Orders orders);

    //orderItem이 모두 삭제된 빈 orders를 삭제
    //select o from Orders o where o.shop.id = :shopId and o.orderItems is empty
    public void deleteEmptyOrders(Long shopId);
    public Orders findById(Long orderId);

    //select m from Orders m where m.shop.id = :shopId
    public List<Orders> findListByShopId(Long shopId);

    //select m from Orders m where m.shop.id = :shopId and m.status = false
    public List<Orders> findListByShopIdAndFalse(Long shopId);
}