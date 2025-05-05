package ego.websocketgateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry reg) {
		reg.enableSimpleBroker("/topic", "/queue")
			.setHeartbeatValue(new long[]{10000, 10000});
		reg.setApplicationDestinationPrefixes("/app");
		reg.setUserDestinationPrefix("/user");

	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry reg) {
		reg.addEndpoint("/ws")
			.setAllowedOriginPatterns("*")
			.withSockJS()
			.setHeartbeatTime(10000);
	}
}
