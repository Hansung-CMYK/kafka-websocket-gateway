package ego.websocketgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public ThreadPoolTaskScheduler brokerHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        return scheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // application prefix
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");

        registry.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(brokerHeartbeatScheduler())
                .setHeartbeatValue(new long[]{10_000, 10_000});
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration reg) {
        int twoMb = 2 * 1024 * 1024;          // 2 MB
        reg.setMessageSizeLimit(twoMb)        // inbound STOMP frame
            .setSendBufferSizeLimit(twoMb)     // outbound STOMP frame
            .setSendTimeLimit(20_000);         // 20 s
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        int twoMb = 2 * 1024 * 1024;   // 2 MB
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(twoMb);
        container.setMaxBinaryMessageBufferSize(twoMb);

        log.info("Tomcat WS buffer = {} bytes", twoMb);
        return container;
    }
}

