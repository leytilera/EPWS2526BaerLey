package de.thkoeln.chessfed.websockets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IUserInteractionService;
import tools.jackson.databind.ObjectMapper;

public class WebsocketHandler extends TextWebSocketHandler {

    private Map<UUID, Set<WebSocketSession>> sessions = new HashMap<>();
    private Map<WebSocketSession, UUID> sessionToUser = new HashMap<>();
    private IUserInteractionService interactionService;
    private ILocalUserRepository userRepository;
    private ObjectMapper json = new ObjectMapper();

    public WebsocketHandler(IUserInteractionService interactionService, ILocalUserRepository userRepository) {
        this.interactionService = interactionService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UriComponents components = UriComponentsBuilder.fromUri(session.getUri()).build();
        String userId = components.getQueryParams().getFirst("user");
        LocalUser user = interactionService.getUser(userId);
        if (!sessions.containsKey(user.getId())) {
            sessions.put(user.getId(), new HashSet<>());
        } 
        sessions.get(user.getId()).add(session);
        sessionToUser.put(session, user.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UUID userId = sessionToUser.remove(session);
        if (userId != null) {
            sessions.get(userId).remove(session);
        }
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UUID userId = sessionToUser.get(session);
        LocalUser user = userRepository.findById(userId).get();
        SocketMessage msg = json.readValue(message.getPayload(), SocketMessage.class);
        MessageType type = MessageType.parse(msg.getType());
        switch (type) { //TODO: handle message
            case CHALLENGE_ACCEPT: {
                try {
                    interactionService.acceptInvitation(user, msg.getContext());
                } catch (Exception e) {
                    sendError(session, e.getMessage());
                }
            } break;
            case MOVE: {
                try {
                    String source = (String) msg.getData().get("source");
                    String target = (String) msg.getData().get("target");
                    String promote = (String) msg.getData().get("promote");
                    interactionService.playMove(user, msg.getContext(), source, target, promote);
                } catch (Exception e) {
                    sendError(session, e.getMessage());
                }
            } break;
            default:{
                sendError(session, "Message type not supported for client->server: " + type);
            } break;
        }
        
        super.handleTextMessage(session, message);
    }

    private void sendError(WebSocketSession session, String message) throws IOException {
        SocketMessage ret = new SocketMessage(-1, null);
        ret.getData().put("error", message);
        TextMessage toSend = new TextMessage(json.writeValueAsString(ret));
        session.sendMessage(toSend);
    }

}
