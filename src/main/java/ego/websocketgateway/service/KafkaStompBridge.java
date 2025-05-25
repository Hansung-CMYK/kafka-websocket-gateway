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

	@KafkaListener(topics = "chat-client-responses", groupId = "gateway-group-client")
	public void listenClient(ChatMessage msg) {
		saver.save(msg, "u");
	}

	@KafkaListener(topics = "chat-ai-responses", groupId = "gateway-group-ai")
	public void listenAI(ChatMessage msg) {
		saver.save(msg, "e");
	}

	@KafkaListener(topics = "chat-responses", groupId = "gateway-group")
	public void listen(ChatMessage msg) {
		ws.convertAndSend("/topic/messages/" + msg.getTo(), msg);

		saver.save(msg, "e");
	}
}
