package capstone.waitingTimekiosk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class MenuItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Long id;

    //메뉴를 소유한 메뉴판 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    private String menuName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    private int price;

    private int defaultTime; //기본 대기 시간

    private int eventTime; //이벤트 설정 대기 시간

    private int eventQuantity; //이벤트 설정 수량

    private String imagePath; //메뉴 이미지가 저장된 파일 경로

    private String description;

    //해당 메뉴와 연결된 주문목록
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    protected MenuItem() {

    }

    public MenuItem(Shop shop) {
        this.shop = shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setMenuItem(this);
    }
}
