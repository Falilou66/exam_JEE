package sn.samabank.samabank_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.OperationRequest;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.service.OperationService;

/**
 * Opérations de caisse (dépôt / retrait) — réservées au conseiller (agence).
 */
@RestController
@RequestMapping("/api/v1/comptes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONSEILLER')")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/{compteId}/depot")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposer(@PathVariable Long compteId,
            @Valid @RequestBody OperationRequest request) {
        return operationService.deposer(compteId, request);
    }

    @PostMapping("/{compteId}/retrait")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse retirer(@PathVariable Long compteId,
            @Valid @RequestBody OperationRequest request) {
        return operationService.retirer(compteId, request);
    }
}
