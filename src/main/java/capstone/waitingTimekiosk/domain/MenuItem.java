package capstone.waitingTimekiosk.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class MenuItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuItem_id")
    private Long id;

    //메뉴를 소유한 메뉴판 식별
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu_id;

    private String menuName;

    private String category;

    private int price;

    private int default_time; //기본 대기 시간

    private int event_time; //이벤트 설정 대기 시간

    private int event_quantity; //이벤트 설정 수량

    private Long image_path; //메뉴 이미지가 저장된 파일 경로
}
