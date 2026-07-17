package sn.samabank.samabank_backend.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.dto.VirementRequest;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.service.VirementService;

/**
 * Virements (SEQ-03) — initiés par le client depuis l'un de ses comptes.
 * Réponse 201 (exécuté) ou 202 (en attente de validation, RG-4).
 */
@RestController
@RequestMapping("/api/v1/virements")
@RequiredArgsConstructor
public class VirementController {

    private final VirementService virementService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TransactionResponse> effectuer(
            @Valid @RequestBody VirementRequest request, Principal principal) {
        TransactionResponse response = virementService.effectuer(request, principal.getName());
        HttpStatus status = response.statut() == StatutTransaction.EN_ATTENTE_VALIDATION
                ? HttpStatus.ACCEPTED
                : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }
}
