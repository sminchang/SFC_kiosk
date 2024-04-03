package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Boolean check; //주문 제공 여부

    private LocalDateTime date; //주문 등록 시간, 수요도 지표에 활용

    //주문표 내 주문목록
    @OneToMany(mappedBy = "order_id")
    private List<OrderItem> orders = new ArrayList<>();

}
