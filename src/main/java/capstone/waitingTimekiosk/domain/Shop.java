package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Shop {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    //메뉴판을 소유한 멤버 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String shopName;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Category> categories = new ArrayList<>();

    protected Shop() {
    }

    public Shop(Member member, String shopName) {
        this.member = member;
        this.shopName = shopName;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        menuItem.setShop(this);
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.setShop(this);
    }

}
