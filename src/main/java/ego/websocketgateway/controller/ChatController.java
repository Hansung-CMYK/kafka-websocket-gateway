package ego.websocketgateway.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import ego.websocketgateway.dto.ChatMessage;
import ego.websocketgateway.service.ChatHistoryJdbcSaver;
import ego.websocketgateway.service.S3Service;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final KafkaTemplate<String, ChatMessage> kafka;
	private final ChatHistoryJdbcSaver saver;
	private final S3Service s3Service;

	@MessageMapping("/chat.send")
	public void sendMessage(@Payload ChatMessage msg) {
		kafka.send("chat-requests", msg.getFrom(), msg);

		try {
			if(msg.getMessageType() == ChatMessage.MessageType.IMAGE) {
				String imageUrl = s3Service.uploadBase64Image(msg.getContent());
				msg.setContent(imageUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		saver.save(msg, "user");
	}
}
