package ego.websocketgateway.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ego.websocketgateway.dto.ChatMessage;

@Component
public class ChatHistoryJdbcSaver {
	private final JdbcTemplate tenantJdbc;

	public ChatHistoryJdbcSaver(
		@Qualifier("tenantJdbcTemplate") JdbcTemplate tenantJdbc
	) {
		this.tenantJdbc = tenantJdbc;
	}

	public void save(ChatMessage msg, String type) {
		String uid = "u".equals(type) ? msg.getFrom() : msg.getTo();
		String sql = String.format(
			"INSERT INTO \"%s\".\"chat_history\" " +
				"(uid, chat_room_id, content, type, content_type, chat_at, is_deleted, message_hash) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
			uid
		);

		tenantJdbc.update(sql,
			msg.getFrom(),
			msg.getChatRoomId(),
			msg.getContent(),
			type.equals("u") ? "u" : "e",
			msg.getContentType().toString(),
			Timestamp.valueOf(msg.getChatAt()),
			msg.isDeleted(),
			msg.getMessageHash()
		);
	}
}
