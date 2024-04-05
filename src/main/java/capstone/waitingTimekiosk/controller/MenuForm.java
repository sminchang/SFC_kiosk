package capstone.waitingTimekiosk.controller;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class MenuForm {

/*
    private Long id;

    private Long menuId;
*/

    private String menuName;

    private String categoryName;

    private int price;

    private int defaultTime; //기본 대기 시간

    /*private int eventTime; //이벤트 설정 대기 시간

    private int eventQuantity; //이벤트 설정 수량
*/
    private Long imagePath; //메뉴 이미지가 저장된 파일 경로

    private String description;
}
