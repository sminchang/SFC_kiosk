package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    //@Column(unique = true, nullable = false)
    private String email;

    private String nickname;

    //회원이 만든 메뉴판 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Shop> shops = new ArrayList<>();


    protected Member() {

    }

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void addMenu(Shop menu) {
        shops.add(menu);
        menu.setMember(this);
    }
}
