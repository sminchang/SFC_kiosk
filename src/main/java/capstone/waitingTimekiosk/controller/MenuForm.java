package capstone.waitingTimekiosk.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter @Setter
public class MenuForm {

    private String menuName;

    private String categoryName;

    private int price;

    private int defaultTime; //기본 대기 시간

    private int eventTime; //이벤트 설정 대기 시간

    private int eventQuantity; //이벤트 설정 수량

    private MultipartFile image; //메뉴 이미지 파일

    private String description;
}
