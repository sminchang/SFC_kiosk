package capstone.waitingTimekiosk.controller;

import capstone.waitingTimekiosk.repository.MemberRepository;
import capstone.waitingTimekiosk.service.KakaoApi;
import jakarta.servlet.annotation.WebServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
class authControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoApi kakaoApi;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    void loginTest() throws Exception {

        String mockCode = "mockCode";
        String mockAccessToken = "mockAccessToken";

    }

}