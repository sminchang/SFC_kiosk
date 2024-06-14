package capstone.waitingTimekiosk.WebSocket;

import capstone.waitingTimekiosk.controller.OrderDTO;
import capstone.waitingTimekiosk.domain.Orders;
import capstone.waitingTimekiosk.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class OrderWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final OrderService orderService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String orderData = message.getPayload();

        // OrderDTO 객체로 변환
        OrderDTO orderDTO = objectMapper.readValue(orderData, OrderDTO.class);

        // OrderService의 메서드 호출하여 주문 생성
        Orders order = orderService.createOrder(orderDTO.getShopId(), orderDTO);

        // 생성된 orderId를 포함하여 orderState.html로 전송
        orderDTO.setOrderId(order.getId());

        // orderDTO를 JSON 문자열로 변환하여 전송
        String jsonMessage = objectMapper.writeValueAsString(orderDTO);

        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(jsonMessage));
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}