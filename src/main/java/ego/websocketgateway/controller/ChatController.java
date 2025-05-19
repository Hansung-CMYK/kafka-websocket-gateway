package ego.websocketgateway.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import ego.websocketgateway.dto.ChatMessage;
import ego.websocketgateway.service.ChatHistoryJdbcSaver;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final KafkaTemplate<String, ChatMessage> kafka;
	private final ChatHistoryJdbcSaver saver;

	@MessageMapping("/chat.send")
	public void sendMessage(@Payload ChatMessage msg) {
		kafka.send("chat-requests", msg.getFrom(), msg);

		saver.save(msg, "user");
	}
}
