package de.thkoeln.chessfed.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import de.thkoeln.chessfed.services.IUserInteractionService;

@Configuration
@EnableWebSocket
public class WebsocketsConfig implements WebSocketConfigurer {

    private IUserInteractionService interactionService;

    @Autowired
    public WebsocketsConfig(IUserInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebsocketHandler(interactionService), "/api/socket").setAllowedOrigins("*");
    }
    
}
