package de.thkoeln.chessfed.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketsConfig implements WebSocketConfigurer {

    private WebsocketHandler handler;

    @Autowired
    public WebsocketsConfig(WebsocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api/socket").setAllowedOrigins("*");
    }
    
}
