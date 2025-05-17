package ego.websocketgateway.controller;

import java.security.Principal;

import ego.websocketgateway.dto.ChatMessage;
import ego.websocketgateway.service.ChatHistoryJdbcSaver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatController {

	private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

	public ChatController(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	// 클라이언트 -> 서버 : /app/chat.send
	@MessageMapping("/chat.send")
	public void sendMessage(@Payload ChatMessage msg) {
		kafkaTemplate.send("chat-requests", msg.getFrom(), msg);

		ChatHistoryJdbcSaver.save(
			msg.getFrom(),
			msg.getChatRoomId(),
			msg.getContent(),
			msg.getMessageType(),
			"U",
			msg.getChatAt(),
			msg.getHash(),
			msg.isDeleted()
		);
	}
}
