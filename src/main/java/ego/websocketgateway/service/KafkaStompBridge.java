package ego.websocketgateway.service;

import ego.websocketgateway.dto.ChatMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaStompBridge {

	private final SimpMessagingTemplate messagingTemplate;

	public KafkaStompBridge(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@KafkaListener(topics = "chat-responses", groupId = "gateway-group")
	public void listen(ChatMessage msg) {
		messagingTemplate.convertAndSendToUser(
			msg.getTo(),
			"/queue/messages",
			msg
		);
	}
}
