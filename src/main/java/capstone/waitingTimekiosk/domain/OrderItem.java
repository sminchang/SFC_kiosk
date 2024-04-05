package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    //주문이 속한 주문표 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    //주문이 가지는 메뉴 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    private int quantity; //주문 수량

    protected OrderItem() {

    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
