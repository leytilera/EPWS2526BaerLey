package de.thkoeln.chessfed.services;

import java.util.UUID;

import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.LocalUser;

public interface IUserInteractionService {
    
    void createInvitation(LocalUser user, String opponent);

    void playMove(LocalUser user, UUID gameId, String sourceField, String targetField, String promote);

    void acceptInvitation(LocalUser user, UUID challengeId);

    UUID[] getGames(LocalUser user);

    Challenge[] getOpenChallenges(LocalUser user);

    LocalUser getUser(String externalId);

}
