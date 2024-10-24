package capstone.waitingTimekiosk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    private LocalDateTime orderTime; //주문 등록 시간, 수요도 지표에 활용, 예상 대기 시간 ai 모델에서 활용

    @Setter
    private LocalDateTime providedTime; //주문 제공 시간, null로 초기화

    @Setter
    private String backOrderIds; //주문 시점에 밀려있던 주문 목록->추후 예상 대기 시간 예측 AI 모델에서 활용할 데이터

    //주문표 내 주문목록
    @OneToMany(mappedBy = "orders", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    protected Orders() {}

    public Orders(Shop shop) {
        this.shop = shop;
        this.orderTime = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrders(this);
    }
}
