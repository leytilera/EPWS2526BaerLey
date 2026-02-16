package de.thkoeln.chessfed.services;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;

@Service
public class LocalUserService extends OidcUserService {

    private ILocalUserRepository userRepository;
    private IActorService actorService;
    @Value("${username.attritube}")
    private String usernameAttritube;

    @Autowired
    public LocalUserService(ILocalUserRepository userRepository, IActorService actorService) {
        this.userRepository = userRepository;
        this.actorService = actorService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        
        String username = Optional.ofNullable(oidcUser.getClaimAsString(usernameAttritube)).map((u) -> u.replace(" ", "_")).orElseGet(oidcUser::getSubject);

        LocalUser user = userRepository.getByExternalOrUsername(oidcUser.getSubject(), username).orElseGet(() -> actorService.createUser(username));
        if (user.getExternal() == null) {
            user.setExternal(oidcUser.getSubject());
        }
        userRepository.save(user);
        return new DefaultOidcUser(Collections.emptySet(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
    
}
