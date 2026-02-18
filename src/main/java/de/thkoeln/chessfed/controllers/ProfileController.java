package de.thkoeln.chessfed.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import de.thkoeln.chessfed.dto.EditProfileDto;
import de.thkoeln.chessfed.dto.ProfileViewModel;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.LocalUserService;
import de.thkoeln.chessfed.services.IFederationService;

@Controller
public class ProfileController {

    private final LocalUserService localUserService;
    private final IActorService actorService;
    private final IFederationService federationService;

    public ProfileController(LocalUserService localUserService,
                             IActorService actorService,
                             IFederationService federationService) {
        this.localUserService = localUserService;
        this.actorService = actorService;
        this.federationService = federationService;
    }

    private ProfileViewModel toViewModel(LocalUser user) {
        ProfileViewModel viewModel = new ProfileViewModel();
        viewModel.setUsername(user.getUsername());
        viewModel.setBio(user.getBio());

        Actor actor = user.getActor();
        if (actor != null) {
            viewModel.setHandle(actor.getLocalpart() + "@" + actor.getDomain());
        } else {
            viewModel.setHandle(user.getUsername() + "@" + federationService.getDomain());
        }

        return viewModel;
    }

    private boolean isSameUser(OidcUser oidcUser, LocalUser user) {
        return user.getExternal() != null && user.getExternal().equals(oidcUser.getSubject());
    }

    private String normalizeAcct(String acctParam) {
        String trimmed = acctParam.trim();
        if (trimmed.contains("@")) {
            return trimmed;
        } else {
            return trimmed + "@" + federationService.getDomain();
        }
    }

    @GetMapping("/profile")
    public String showOwnProfile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        LocalUser localUser = localUserService.getLocalUser(oidcUser);

        ProfileViewModel viewModel = toViewModel(localUser);
        viewModel.setOwnProfile(true);

        model.addAttribute("profile", viewModel);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editOwnProfile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        LocalUser localUser = localUserService.getLocalUser(oidcUser);

        ProfileViewModel viewModel = toViewModel(localUser);
        viewModel.setOwnProfile(true);
        EditProfileDto form = new EditProfileDto();
        form.setBio(localUser.getBio());
    
        model.addAttribute("profile", viewModel);
        model.addAttribute("form", form);
        return "edit-profile";
    }


    @PostMapping("/profile/edit")
    public String saveOwnProfile(@AuthenticationPrincipal OidcUser oidcUser, @ModelAttribute("form") EditProfileDto editProfileDto) {
        LocalUser localUser = localUserService.getLocalUser(oidcUser);

        String bio = editProfileDto.getBio();
        if (bio != null) {
            bio = bio.trim();
            if (bio.length() > 280) {
                bio = bio.substring(0, 280);
            }
        }
        localUser.setBio(bio);
        localUserService.saveLocalUser(localUser);

        return "redirect:/profile";
    }

    @GetMapping("/users")
    public String showOtherProfile(@AuthenticationPrincipal OidcUser oidcUser, @RequestParam("acct") String acctParam, Model model) {
        
        if (acctParam == null || acctParam.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "acct is required");
        }

        String normalizedAcct = normalizeAcct(acctParam);
        String acct = "acct:" + normalizedAcct;
        Actor actor;
        try {
            actor = actorService.getActorByAcct(acct);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Optional<LocalUser> localUser = localUserService.getLocalUserByActor(actor);

        ProfileViewModel viewModel;
        if (localUser.isPresent()) {
            LocalUser user = localUser.get();
            viewModel = toViewModel(user);
            boolean isOwnProfile = (oidcUser != null) && isSameUser(oidcUser, user);
            viewModel.setOwnProfile(isOwnProfile);
        } else {
            viewModel = new ProfileViewModel();
            viewModel.setId(null);
            viewModel.setUsername(actor.getLocalpart());
            viewModel.setHandle(actor.getLocalpart() + "@" + actor.getDomain());
            viewModel.setBio(null);
            viewModel.setOwnProfile(false);
        }

        model.addAttribute("profile", viewModel);
        return "profile";
    }

}
