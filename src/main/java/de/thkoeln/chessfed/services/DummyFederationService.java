package de.thkoeln.chessfed.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DummyFederationService implements IFederationService {

    @Value("${federation.domain}")
    private String domain;
    @Value("${federation.baseUrl}")
    private String baseUrl;
    
    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getDomain() {
        return domain;
    }

}
