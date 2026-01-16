package de.thkoeln.chessfed.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.util.UUID

class PlayerSessionHandshakeInterceptor: HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val servletRequest = (request as? ServletServerHttpRequest)?.servletRequest ?: return false
        val httpSession = servletRequest.getSession(true)
        val playerKey = (httpSession.getAttribute("playerKey") as String?) ?: UUID.randomUUID().toString().also {
            httpSession.setAttribute("playerKey", it)
        }
        attributes["playerKey"] = playerKey

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // No implementation needed after handshake
    }
}