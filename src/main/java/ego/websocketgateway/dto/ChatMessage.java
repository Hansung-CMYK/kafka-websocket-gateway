package ego.websocketgateway.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {
	private int chatRoomId;
	private String from;
	private String content;
	private MessageType type;
	private String hash;

	@Builder.Default
	private Instant chat_at = Instant.now();
	@Builder.Default
	private boolean isDeleted = false;

	private String to;

	@Setter(AccessLevel.NONE)
	private boolean mcpEnabled;

	@Builder
	public ChatMessage(int chatRoomId, String from, String content, MessageType type, String hash, Instant chat_at, boolean isDeleted, String to, boolean mcpEnabled) {
		this.chatRoomId = chatRoomId;
		this.from = from;
		this.content = content;
		this.type = type;
		this.hash = hash;
		this.chat_at = chat_at;
		this.isDeleted = false;
		this.to = to;
		this.mcpEnabled = type == MessageType.TEXT && mcpEnabled; // 텍스트일 때만 유효
		this.chat_at = chat_at != null ? chat_at : Instant.now();
	}

	public enum MessageType {
		TEXT,
		IMAGE
	}
}
