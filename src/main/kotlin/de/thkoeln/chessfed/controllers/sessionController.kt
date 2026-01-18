package de.thkoeln.chessfed.controllers

import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import de.thkoeln.chessfed.services.ActorService

@RestController
@RequestMapping("/api/session")
class SessionController(
    private val actorService: ActorService
) {

    @PostMapping("/anonymous")
    fun createNewSession(session: HttpSession): Map<String, String> {
        val existingSession = session.getAttribute("playerKey") as? String
        if (!existingSession.isNullOrBlank()) {
            return mapOf("playerKey" to existingSession)
        }

        /* eindeutigen username erstellen, syntax: user12345678 -> später richtigen username verwenden */
        val username = "user" + UUID.randomUUID().toString().substring(0, 8)

        val localUser = actorService.createUser(username)

        val playerKey = localUser.id.toString()
        session.setAttribute("playerKey", playerKey)

        return mapOf(
            "playerKey" to playerKey,
            "username" to localUser.username,
            "actorUrl" to (localUser.actor?.id ?: "")
        )
    }
}