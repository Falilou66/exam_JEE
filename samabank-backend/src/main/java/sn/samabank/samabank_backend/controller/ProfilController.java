package sn.samabank.samabank_backend.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.ClientResponse;
import sn.samabank.samabank_backend.dto.ProfilUpdateRequest;
import sn.samabank.samabank_backend.service.ProfilService;

/**
 * Gestion de son propre profil par le client (SEQ-12, BF-06).
 */
@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
public class ProfilController {

    private final ProfilService profilService;

    @PutMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ClientResponse mettreAJour(@Valid @RequestBody ProfilUpdateRequest request,
            Principal principal) {
        return profilService.mettreAJour(principal.getName(), request);
    }
}
