package de.thkoeln.chessfed.config

import de.thkoeln.chessfed.handlers.ChessGameHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import jakarta.annotation.PostConstruct

@Configuration
@EnableWebSocket
class WebSocketConfig: WebSocketConfigurer {

    @PostConstruct
        fun init() {
        println("### WebSocketConfig LOADED ###")
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChessGameHandler(), "/game")
        .addInterceptors(PlayerSessionHandshakeInterceptor())
        // "http://localhost:8080", "https://deine-domain.tld"
        .setAllowedOrigins("*") // später einschränken für Sicherheit
    }

    @Bean
    fun chessGameHandler(): ChessGameHandler = ChessGameHandler()
}