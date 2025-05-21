package ego.websocketgateway.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ego.websocketgateway.dto.ChatMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaStompBridge {

	private final SimpMessagingTemplate ws;
	private final ChatHistoryJdbcSaver saver;

	@KafkaListener(topics = "chat-responses", groupId = "gateway-group")
	public void listen(ChatMessage msg) {
		ws.convertAndSend("/topic/messages/" + msg.getTo(), msg);

		saver.save(msg, "e");
	}
}
