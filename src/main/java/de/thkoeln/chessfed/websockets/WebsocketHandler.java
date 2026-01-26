package de.thkoeln.chessfed.websockets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import de.thkoeln.chessfed.events.MoveEvent;
import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IUserInteractionService;
import tools.jackson.databind.ObjectMapper;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private Map<UUID, Set<WebSocketSession>> sessions = new HashMap<>();
    private Map<WebSocketSession, UUID> sessionToUser = new HashMap<>();
    private IUserInteractionService interactionService;
    private ILocalUserRepository userRepository;
    private ObjectMapper json = new ObjectMapper();

    @Autowired
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

    private void sendToUser(UUID userId, SocketMessage message) {
        String encoded = json.writeValueAsString(message);
        if (sessions.containsKey(userId)) {
            sessions.get(userId).forEach((session) -> {
                try {
                    session.sendMessage(new TextMessage(encoded));
                } catch (Exception e) {
                    // :P
                }
            });
        }
    }

    @EventListener
    public void onMove(MoveEvent event) {
        SocketMessage msg = new SocketMessage();
        msg.setType(MessageType.MOVE.ordinal());
        msg.setContext((UUID) event.getGame().getId());
        msg.setData(new HashMap<>());
        msg.getData().put("source", event.getSource());
        msg.getData().put("target", event.getTarget());
        msg.getData().put("promote", event.getPromote());
        userRepository.getByActor(event.getOpponent()).ifPresent((usr) -> sendToUser(usr.getId(), msg));
    }

}