package ego.websocketgateway.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatHistoryJdbcSaver {
	@Value("${app.postgres.url}")
	private static String url;

	@Value("${app.postgres.user}")
	private static String user;

	@Value("${app.postgres.password}")
	private static String password;

	private static final String INSERT_SQL = """
        INSERT INTO chat_history (uid, chat_room_id, content, type, chat_at, hash, is_deleted)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

	public static void save(
		String uid, int chatRoomId, String content,
		String type, java.time.Instant chatAt,
		String hash, boolean isDeleted
	) {
		try (
			Connection conn = DriverManager.getConnection(url, user, password);
			PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)
		) {
			stmt.setString(1, uid);
			stmt.setInt(2, chatRoomId);
			stmt.setString(3, content);
			stmt.setString(4, type);
			stmt.setTimestamp(5, Timestamp.from(chatAt));
			stmt.setString(6, hash);
			stmt.setBoolean(7, isDeleted);

			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace(); // log.error 로 바꿀 수 있음
		}
	}
}
