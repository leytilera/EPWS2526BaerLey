package de.thkoeln.chessfed.handlers

import org.json.JSONObject
import org.json.JSONArray
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder
import java.util.concurrent.ConcurrentHashMap

class ChessGameHandler: TextWebSocketHandler() {

    private data class GameRoom(
        val sessions: ArrayList<WebSocketSession> = ArrayList(),
        var whitePlayerKey: String? = null,
        var blackPlayerKey: String? = null,
        var turn: String = "WHITE",
        val moves: MutableList<JSONObject> = mutableListOf()
    )

    private val rooms: ConcurrentHashMap<String, GameRoom> = ConcurrentHashMap()

    private fun playerKeyOfSession(session: WebSocketSession): String? {
        val attributes = session.attributes
        return attributes["playerKey"] as? String
    }

    private fun getGameIdFromSession(session: WebSocketSession): String? {
        val uri = session.uri ?: return null
        val queryParams = UriComponentsBuilder.fromUri(uri).build().queryParams
        val gameId = queryParams.getFirst("gameId")
        return gameId
    }

    private fun colorOfSession(session: WebSocketSession, room: GameRoom): String? {
        val playerKey = playerKeyOfSession(session) ?: return null
        return when (playerKey) {
            room.whitePlayerKey -> "WHITE"
            room.blackPlayerKey -> "BLACK"
            else -> null
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val gameId = getGameIdFromSession(session) ?: "default"
        val room = rooms.computeIfAbsent(gameId) { GameRoom() }

        val playerKey = playerKeyOfSession(session)
        if (playerKey.isNullOrBlank()) {
            val error = JSONObject()
            error.put("type", "ERROR")
            error.put("message", "Missing playerKey in session attributes")
            safeSendMessage(session, TextMessage(error.toString()))
            session.close(CloseStatus.SERVER_ERROR)
            return
        }

        val assignMessage = JSONObject()
        assignMessage.put("type", "JOINED")

        synchronized(room) {
            room.sessions.add(session)

            val color = when {
                room.whitePlayerKey == playerKey -> "WHITE"
                room.blackPlayerKey == playerKey -> "BLACK"
                room.whitePlayerKey == null -> {
                    room.whitePlayerKey = playerKey
                    "WHITE"
                }
                room.blackPlayerKey == null -> {
                    room.blackPlayerKey = playerKey
                    "BLACK"
                }
                else -> {
                    val error = JSONObject()
                    error.put("type", "ERROR")
                    error.put("message", "Game room is full")
                    session.sendMessage(TextMessage(error.toString()))
                    session.close(CloseStatus.POLICY_VIOLATION)
                    return
                }
            }
            assignMessage.put("color", color)
            assignMessage.put("turn", room.turn)
        }
        safeSendMessage(session, TextMessage(assignMessage.toString()))
        // broadcastGameState(gameId, room)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val gameId = getGameIdFromSession(session) ?: return
        val room = rooms[gameId] ?: return

        val jsonMessage = JSONObject(message.payload)
        val type = jsonMessage.getString("type")

        synchronized(room) {
            when (type) {
                "SNAPSHOT_REQUEST" -> {
                    sendSnapshot(session, room)
                }
                "MOVE_SUBMIT" -> {
                    val bothPlayersPresent = (room.whitePlayerKey != null && room.blackPlayerKey != null)
                    if (!bothPlayersPresent) {
                        sendRejection(session, "Waiting for opponent")
                        return
                    }

                    val move = jsonMessage.getString("move")
                    if (move.isBlank()) {
                        sendRejection(session, "Missing move")
                        return
                    }
                    val color = colorOfSession(session, room)
                    if (color == null) {
                        sendRejection(session, "You are not assigned a color")
                        return
                    }

                    if (room.turn != color) {
                        sendRejection(session, "It's not your turn")
                        return
                    }

                    // ADD MOVE VALIDATION HERE LATER!
                    val moveObject = JSONObject()
                    moveObject.put("color", color)
                    moveObject.put("move", move)
                    moveObject.put("index", room.moves.size)
                    room.moves.add(moveObject)

                    room.turn = if (room.turn == "WHITE") "BLACK" else "WHITE"

                    val commitMessage = JSONObject()
                    commitMessage.put("type", "MOVE_COMMITTED")
                    commitMessage.put("color", color)
                    commitMessage.put("move", move)
                    commitMessage.put("turn", room.turn)
                    commitMessage.put("index", moveObject.getInt("index"))

                    room.sessions.forEach { session ->
                        safeSendMessage(session, TextMessage(commitMessage.toString()))
                    }
                    // broadcastGameState(gameId, room)
                }
                else -> {
                    val error = JSONObject()
                    error.put("type", "ERROR")
                    error.put("message", "Unknown message type: $type")
                    safeSendMessage(session, TextMessage(error.toString()))
                }
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val gameId = getGameIdFromSession(session) ?: return
        val room = rooms[gameId] ?: return

        synchronized(room) {
            room.sessions.removeIf {it.id == session.id}

            if (room.sessions.isEmpty()) {
                rooms.remove(gameId)
            } else {
                // broadcastGameState(gameId, room)
            }
        }
    }

    private fun safeSendMessage(session: WebSocketSession, message: TextMessage) {
        synchronized(session) {
            if (session.isOpen) {
                session.sendMessage(message)
            }
        }
    }

    private fun broadcastGameState(gameId: String, room: GameRoom) {
        val gameState = JSONObject()
        gameState.put("type", "GAME_STATE")
        gameState.put("turn", room.turn)
        gameState.put("moves", room.moves)

        val message = TextMessage(gameState.toString())
        for (session in room.sessions) {
            session.sendMessage(message)
        }
    }

    private fun sendSnapshot(session: WebSocketSession, room: GameRoom) {
        val snapshot = JSONObject()
        snapshot.put("type", "SNAPSHOT")
        snapshot.put("turn", room.turn)
        snapshot.put("moveCount", room.moves.size)
        snapshot.put("moves", JSONArray(room.moves))

        safeSendMessage(session, TextMessage(snapshot.toString()))
    }

    private fun sendRejection(session: WebSocketSession, reason: String) {
        val rejectMessage = JSONObject()
        rejectMessage.put("type", "MOVE_REJECTED")
        rejectMessage.put("reason", reason)
        safeSendMessage(session, TextMessage(rejectMessage.toString()))
    }
}