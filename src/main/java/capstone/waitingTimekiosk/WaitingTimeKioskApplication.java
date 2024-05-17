package capstone.waitingTimekiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//springboot,JPA,thymeleaf 사용법: 인프런,김영한 강의 시리즈
//카카오 로그인 참조: https://velog.io/@kbk3771/Spring-Boot-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
//카카오 로그인 참조: https://innovation123.tistory.com/181
//git 사용법: https://velog.io/@zerokick/IntelliJ-IntelliJ-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-GitHub-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0
//이미지 동적 업로드 설정: https://g4daclom.tistory.com/77
//mySql 연동: https://velog.io/@dionisos198/Spring-boot-%EC%99%80-MySQL-workbench-%EC%97%B0%EA%B2%B0%ED%95%B4%EB%B3%B4%EA%B8%B0
//이외의 대부분 참조 코드: claude.ai 질의 반복

@SpringBootApplication
public class WaitingTimeKioskApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaitingTimeKioskApplication.class, args);
	}

}
