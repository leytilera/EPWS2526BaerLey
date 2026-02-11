package de.thkoeln.chessfed.services;

import java.util.UUID;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.ObjectType;

public interface IFederationService {

    String getBaseUrl();

    String getDomain();

    FederatedObject createFederatedObject(String url, ObjectType type);

    FederatedObject createFederatedObject(UUID id, ObjectType type);

    FederatedObject getFederatedObject(String url);

    boolean isLocal(FederatedObject object);
    
}
