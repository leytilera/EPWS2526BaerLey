package de.thkoeln.chessfed.config

import de.thkoeln.chessfed.handlers.FederatedChessGameHandler
import de.thkoeln.chessfed.services.IActorService
import de.thkoeln.chessfed.services.IChessGameService
import de.thkoeln.chessfed.model.ILocalUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chessGameService: IChessGameService,
    private val actorService: IActorService
    private val localUserRepository: ILocalUserRepository
): WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
        .addHandler(FederatedChessGameHandler(chessGameService, actorService, localUserRepository), "/game")
        .addInterceptors(PlayerSessionHandshakeInterceptor())
        // "http://localhost:8080", "https://deine-domain.tld"
        .setAllowedOrigins("*") // später einschränken für Sicherheit
    }

    @Bean
    fun federatedChessGameHandler(): FederatedChessGameHandler = FederatedChessGameHandler(chessGameService, actorService, localUserRepository)
}