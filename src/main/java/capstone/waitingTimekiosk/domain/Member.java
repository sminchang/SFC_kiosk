package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String nickname;

    protected Member() {

    }

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
