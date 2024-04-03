package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    //메뉴판을 소유한 멤버 식별
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member_id;
}
