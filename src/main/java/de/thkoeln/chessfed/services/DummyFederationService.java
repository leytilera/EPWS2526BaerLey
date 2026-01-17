package de.thkoeln.chessfed.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IFederatedObjectRepository;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class DummyFederationService implements IFederationService {

    @Value("${federation.domain}")
    private String domain;
    @Value("${federation.baseUrl}")
    private String baseUrl;
    
    private IFederatedObjectRepository objectRepository;

    @Autowired
    public DummyFederationService(IFederatedObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public FederatedObject createFederatedObject(String url, ObjectType type) {
        FederatedObject object = new FederatedObject(url, type);
        objectRepository.save(object);
        return object;
    }

    @Override
    public FederatedObject createFederatedObject(UUID id, ObjectType type) {
        String prefix = null;
        switch (type) {
            case ACTIVITY:
                prefix = "/activities/";
                break;
            case ACTOR:
                prefix = "/users/";
                break;
            case GAME:
                prefix = "/games/";
                break;
            default:
                throw new IllegalArgumentException();

        }
        return createFederatedObject(getBaseUrl() + prefix + id.toString(), type);
    }

    @Override
    public FederatedObject getFederatedObject(String url) {
        return objectRepository.findById(url).orElseThrow(ResourceNotFoundException::new);
    }

}
