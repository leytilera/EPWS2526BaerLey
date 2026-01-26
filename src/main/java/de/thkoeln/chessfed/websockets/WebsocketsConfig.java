package de.thkoeln.chessfed.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.services.IUserInteractionService;

@Configuration
@EnableWebSocket
public class WebsocketsConfig implements WebSocketConfigurer {

    private IUserInteractionService interactionService;
    private ILocalUserRepository userRepository;

    @Autowired
    public WebsocketsConfig(IUserInteractionService interactionService, ILocalUserRepository userRepository) {
        this.interactionService = interactionService;
        this.userRepository = userRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebsocketHandler(interactionService, userRepository), "/api/socket").setAllowedOrigins("*");
    }
    
}
