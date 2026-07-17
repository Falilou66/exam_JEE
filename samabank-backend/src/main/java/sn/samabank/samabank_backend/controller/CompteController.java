package sn.samabank.samabank_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.ChangementStatutRequest;
import sn.samabank.samabank_backend.dto.CompteResponse;
import sn.samabank.samabank_backend.dto.OuvertureCompteRequest;
import sn.samabank.samabank_backend.service.CompteService;

/**
 * Gestion des comptes. Le conseiller ouvre les comptes et change leur état
 * (SEQ-07/09) ; le client consulte les siens (BF-02).
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    /** Ouverture d'un compte pour un client (conseiller). */
    @PostMapping("/clients/{clientId}/comptes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CONSEILLER')")
    public CompteResponse ouvrir(@PathVariable Long clientId,
            @Valid @RequestBody OuvertureCompteRequest request) {
        return compteService.ouvrir(clientId, request);
    }

    /** Liste des comptes d'un client donné (conseiller). */
    @GetMapping("/clients/{clientId}/comptes")
    @PreAuthorize("hasRole('CONSEILLER')")
    public List<CompteResponse> comptesDuClient(@PathVariable Long clientId) {
        return compteService.comptesDuClient(clientId);
    }

    /** Blocage / déblocage / clôture d'un compte (conseiller). */
    @PatchMapping("/comptes/{id}/statut")
    @PreAuthorize("hasRole('CONSEILLER')")
    public CompteResponse changerStatut(@PathVariable Long id,
            @Valid @RequestBody ChangementStatutRequest request) {
        return compteService.changerStatut(id, request.statut());
    }

    /** Consultation de ses propres comptes (client connecté). */
    @GetMapping("/comptes")
    @PreAuthorize("hasRole('CLIENT')")
    public List<CompteResponse> mesComptes(Principal principal) {
        return compteService.comptesParEmail(principal.getName());
    }
}
