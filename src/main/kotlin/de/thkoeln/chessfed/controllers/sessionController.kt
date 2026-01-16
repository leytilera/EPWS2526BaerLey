package de.thkoeln.chessfed.controllers

import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/session")
class SessionController {

    @PostMapping("/anonymous")
    fun anonymous(session: HttpSession): ResponseEntity<Map<String, String>> {
        val key = (session.getAttribute("playerKey") as String?) ?: UUID.randomUUID().toString().also { session.setAttribute("playerKey", it) }

        return ResponseEntity.ok(mapOf("status" to "ok", "playerKey" to key))
    }
}