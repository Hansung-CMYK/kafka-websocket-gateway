package ego.websocketgateway.dto;

import java.time.Instant;

import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

	private String from;
	private String to;
	private String content;
	private MessageType type;

	@Setter(AccessLevel.NONE)
	private boolean mcpEnabled;

	@Builder.Default
	private Instant timestamp = Instant.now();

	@Builder
	public ChatMessage(String from, String to, String content, MessageType type, boolean mcpEnabled, Instant timestamp) {
		this.from = from;
		this.to = to;
		this.content = content;
		this.type = type;
		this.mcpEnabled = type == MessageType.TEXT && mcpEnabled; // 텍스트일 때만 유효
		this.timestamp = timestamp != null ? timestamp : Instant.now();
	}

	public enum MessageType {
		TEXT,
		IMAGE
	}
}
