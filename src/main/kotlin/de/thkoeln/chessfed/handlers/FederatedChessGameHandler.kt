package de.thkoeln.chessfed.handlers

import de.thkoeln.chessfed.model.ChessPlayer
import de.thkoeln.chessfed.model.Actor
import de.thkoeln.chessfed.model.LocalUser
import de.thkoeln.chessfed.services.IActorService
import de.thkoeln.chessfed.services.IChessGameService
import de.thkoeln.chessfed.model.ILocalUserRepository
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class FederatedChessGameHandler(
    private val chessGameService: IChessGameService,
    private val actorService: IActorService,
    private val localUserRepository: ILocalUserRepository
) : TextWebSocketHandler() {

    private val subscribers: ConcurrentHashMap<UUID, MutableSet<WebSocketSession>> = ConcurrentHashMap()

    private val sendLocks: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    private fun lockForSession(session: WebSocketSession): Any =
        sendLocks.computeIfAbsent(session.id) { Any() }

    private fun safeSendMessage(session: WebSocketSession, object: JSONObject) {
        if (!session.isOpen) return
        val message = TextMessage(object.toString())
        synchronized(lockForSession(session)) {
            if (session.isOpen) {
                try {
                    session.sendMessage(message)
                } catch (e: Exception) {
                    println("Error sending message to session ${session.id}: ${e.message}")
                }
            }
        }
    }

    private fun getGameId(session: WebSocketSession): UUID? {
        val uri = session.uri ?: return null
        val queryParams = UriComponentsBuilder.fromUri(uri).build().queryParams
        val gameId = queryParams.getFirst("gameId") ?: return null

        return try {
            UUID.fromString(gameId)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /* playerKey == LocalUser.id (= UUID als String) */
    private fun getPlayerKey(session: WebSocketSession): String? {
        val attributes = session.attributes
        return attributes["playerKey"] as? String
    }

    private fun getPlayerColor(session: WebSocketSession, gameId: UUID): String? {
        val playerKey = getPlayerKey(session) ?: return "NONE"

        val userId = try {
            UUID.fromString(playerKey)
        } catch (e: Exception) {
            return "NONE"
        }
        
        val localUser = localUserRepository.findById(userId).orElse(null) ?: return "NONE"
        val actor = localUser.actor ?: return "NONE"

        val game = chessGameService.getGame(gameId)
        val whiteUrl = game.whitePlayer.id
        val blackUrl = game.blackPlayer.id

        return when (actor.id) {
            whiteUrl -> "WHITE"
            blackUrl -> "BLACK"
            else -> "NONE"
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val gameId = getGameId(session)
        if (gameId == null) {
            val error = JSONObject()
            error.put("type", "ERROR")
            error.put("message", "Invalid or missing gameId in query parameters")
            safeSendMessage(session, error)
            session.close(CloseStatus.SERVER_ERROR)
            return
        }

        val subscribersSet = subscribers.computeIfAbsent(gameId) { ConcurrentHashMap.newKeySet() }
        synchronized(subscribersSet) {
            subscribersSet.add(session)
        }

        val color = getPlayerColor(session, gameId) ?: "SPECTATOR"

        val joinMessage = JSONObject()
        joinMessage.put("type", "JOINED")
        joinMessage.put("gameId", gameId.toString())
        joinMessage.put("color", color)
        safeSendMessage(session, joinMessage)

        sendSnapshot(session, gameId)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val gameId = getGameId(session)
        if (gameId == null) {
            val error = JSONObject()
            error.put("type", "ERROR")
            error.put("message", "Invalid or missing gameId in query parameters")
            safeSendMessage(session, error)
            return
        }
        
        val jsonMessage = try {
            JSONObject(message.payload)
        } catch (e: Exception) {
            val error = JSONObject()
            error.put("type", "ERROR")
            error.put("message", "Invalid JSON message")
            safeSendMessage(session, error)
            return
        }

        val type = jsonMessage.optString("type", "")

        when (type) {
            "SNAPSHOT_REQUEST" -> {
                sendSnapshot(session, gameId)
            }
            "MOVE_SUBMIT" -> {
                val source = jsonMessage.optString("source", "")
                val target = jsonMessage.optString("target", "")
                if (source.isBlank() || target.isBlank()) {
                    val rejectMessage = JSONObject()
                    rejectMessage.put("type", "MOVE_REJECTED")
                    rejectMessage.put("reason", "Missing source or target in MOVE_SUBMIT")
                    safeSendMessage(session, rejectMessage)
                    return
                }

                try {
                    val game = chessGameService.getGame(gameId)

                    val color = getPlayerColor(session, gameId)
                    val isPlayersTurn =
                            (color == "WHITE" && game.currentTurn == ChessPlayer.WHITE) || (color == "BLACK" && game.currentTurn == ChessPlayer.BLACK)

                    if (!isPlayersTurn) {
                        val rejectMessage = JSONObject()
                        rejectMessage.put("type", "MOVE_REJECTED")
                        rejectMessage.put("reason", "It's not your turn")
                        safeSendMessage(session, rejectMessage)
                        return
                    }

                    val sourceId = chessGameService.getFieldId(source)
                    val targetId = chessGameService.getFieldId(target)

                    val move = chessGameService.createMove(game, sourceId, targetId)
                    chessGameService.applyMove(move)

                    broadcastSnapshot(gameId)
                } catch (e: Exception) {
                    val rejectMessage = JSONObject()
                    rejectMessage.put("type", "MOVE_REJECTED")
                    rejectMessage.put("reason", e.message ?: "Unknown error")
                    safeSendMessage(session, rejectMessage)
                }
            }
            else -> {
                val error = JSONObject()
                error.put("type", "ERROR")
                error.put("message", "Unknown message type: $type")
                safeSendMessage(session, error)
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val gameId = getGameId(session) ?: return
        subscribers[gameId]?.remove(session)
        if (subscribers[gameId]?.isEmpty() == true) {
            subscribers.remove(gameId)
        }
        sendLocks.remove(session.id)
    }

    private fun sendSnapshot(session: WebSocketSession, gameId: UUID) {
        try {
            val game = chessGameService.getGame(gameId)
            val moves = chessGameService.getMoves(game)

            val movesJson = JSONArray()
            moves.forEach { move ->
                movesJson.put(
                    JSONObject()
                    .put("count", move.moveCount)
                    .put("player", move.player.name)
                    .put("source", chessGameService.getFieldDescriptor(move.sourceField))
                    .put("target", chessGameService.getFieldDescriptor(move.targetField))
                    .put("capture", move.isCapture)
                    .put("castle", move.isCastle)
                    .put("promote", move.promote?.let { p -> p.name })
                )
            }

            val snapshot = JSONObject()
                .put("type", "SNAPSHOT")
                .put("gameId", gameId.toString())
                .put("turn", game.currentTurn.name)
                .put("finished", game.isHasEnded)
                .put("moveCount", moves.size)
                .put("moves", movesJson)

            safeSendMessage(session, snapshot)
        } catch (e: Exception) {
            val error = JSONObject()
            error.put("type", "ERROR")
            error.put("message", "Send snapshot failed")
            safeSendMessage(session, error)
        }
    }

    private fun broadcastSnapshot(gameId: UUID) {
        val setSubscribers = subscribers[gameId] ?: return
        val sessions = setSubscribers.toList()

        sessions.forEach { session ->
            sendSnapshot(session, gameId)
        }
    }

    fun notifyGameChanged(gameId: UUID) {
        broadcastSnapshot(gameId)
    }
}
