package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String categoryName;

    protected Category() {

    }

    public Category(Shop shop, String categoryName) {
        this.shop = shop;
        this.categoryName = categoryName;
    }
}