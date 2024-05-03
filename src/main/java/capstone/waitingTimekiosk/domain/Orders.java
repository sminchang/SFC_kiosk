package capstone.waitingTimekiosk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

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

    @Setter
    private Boolean status; //주문 제공 여부

    private LocalDateTime date; //주문 등록 시간, 수요도 지표에 활용

    //주문표 내 주문목록
    @OneToMany(mappedBy = "orders", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    protected Orders() {
    }

    public Orders(Shop shop) {
        this.shop = shop;
        this.status = false;
        this.date = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrders(this);
    }
}
