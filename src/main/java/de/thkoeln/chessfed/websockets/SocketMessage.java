package de.thkoeln.chessfed.websockets;

import java.util.Map;
import java.util.UUID;

public class SocketMessage {
    
    private int type;
    private UUID context;
    private Map<String, Object> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UUID getContext() {
        return context;
    }

    public void setContext(UUID context) {
        this.context = context;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
