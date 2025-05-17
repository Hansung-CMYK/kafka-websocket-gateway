package ego.websocketgateway.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ego.websocketgateway.dto.ChatMessage;

@Component
public class ChatHistoryJdbcSaver {

	private static final Logger log = LoggerFactory.getLogger(ChatHistoryJdbcSaver.class);

	private static String url;
	private static String user;
	private static String password;

	@Value("${spring.postgresql.url}")
	public void setUrl(String url) {
		ChatHistoryJdbcSaver.url = url;
	}

	@Value("${spring.postgresql.user}")
	public void setUser(String user) {
		ChatHistoryJdbcSaver.user = user;
	}

	@Value("${spring.postgresql.password}")
	public void setPassword(String password) {
		ChatHistoryJdbcSaver.password = password;
	}

	private static final String INSERT_SQL = """
        INSERT INTO chat_history (uid, chat_room_id, content, message_type, type, chat_at, hash, is_deleted)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

	public static void save(
		String uid, int chatRoomId, String content, ChatMessage.MessageType messageType,
		String type, LocalDateTime chatAt, String hash, boolean isDeleted
	) {
		try (
			Connection conn = DriverManager.getConnection(url, user, password);
			PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)
		) {
			stmt.setString(1, uid);
			stmt.setInt(2, chatRoomId);
			stmt.setString(3, content);
			stmt.setString(4, messageType.name());
			stmt.setString(5, type);
			stmt.setTimestamp(6, Timestamp.valueOf(chatAt));
			stmt.setString(7, hash);
			stmt.setBoolean(8, isDeleted);

			stmt.executeUpdate();
		} catch (Exception e) {
			log.error("DB 저장 실패 (uid: {}, chatRoomId: {}): {}", uid, chatRoomId, e.getMessage(), e);
		}
	}
}
