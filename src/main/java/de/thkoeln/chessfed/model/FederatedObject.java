package de.thkoeln.chessfed.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class FederatedObject {

    @Id
    private String id;
    @Enumerated(EnumType.ORDINAL)
    private ObjectType type;

    public FederatedObject(String id, ObjectType type) {
        this.id = id;
        this.type = type;
    }

    public FederatedObject() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }
    
}
