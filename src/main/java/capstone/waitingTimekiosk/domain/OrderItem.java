package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderItem_id")
    private Long id;

    //주문이 속한 주문표 식별
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order_id;

    //주문이 가지는 메뉴 정보
    @ManyToOne
    @JoinColumn(name = "munuItem_id")
    private MenuItem menuItem_id;

    private int quantity; //주문 수량
}
