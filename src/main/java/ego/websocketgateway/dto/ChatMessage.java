package ego.websocketgateway.dto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {
	private int chatRoomId;
	private String from;
	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private MessageType messageType;
	private String hash;

	@Builder.Default
	private LocalDateTime chatAt = LocalDateTime.now();

	@Builder.Default
	private boolean isDeleted = false;

	private String to;

	@Setter(AccessLevel.NONE)
	private boolean mcpEnabled;

	@Builder
	public ChatMessage(int chatRoomId, String from, String content, MessageType messageType, String hash, LocalDateTime chatAt, boolean isDeleted, String to, boolean mcpEnabled) {
		this.chatRoomId = chatRoomId;
		this.from = from;
		this.content = content;
		this.messageType = messageType;
		this.isDeleted = false;
		this.to = to;
		this.mcpEnabled = messageType == MessageType.TEXT && mcpEnabled; // 텍스트일 때만 유효
		this.chatAt = chatAt != null ? chatAt : LocalDateTime.now();
		this.hash = hash == null ? generateHash(from, chatAt, content) : hash;
	}

	public enum MessageType {
		TEXT,
		IMAGE
	}

	public static String generateHash(String uid, LocalDateTime chatAt, String content) {
		String input = uid + "|" + chatAt.toString() + "|" + content;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : encodedHash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
