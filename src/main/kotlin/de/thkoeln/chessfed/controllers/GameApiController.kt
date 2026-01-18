package de.thkoeln.chessfed.controllers

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/games")
class GameApiController {
    data class CreateGameResponse(val gameId: String)

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGame(): CreateGameResponse {
        val gameId = UUID.randomUUID().toString()
        return CreateGameResponse(gameId)
    }
}