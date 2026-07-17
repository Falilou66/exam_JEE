package sn.samabank.samabank_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.dto.ValidationRequest;
import sn.samabank.samabank_backend.service.ValidationService;

/**
 * Validation des opérations sensibles par un conseiller (SEQ-08, BF-10).
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONSEILLER')")
public class ValidationController {

    private final ValidationService validationService;

    @GetMapping("/en-attente")
    public PagedModel<TransactionResponse> enAttente(
            @PageableDefault(size = 20, sort = "dateOperation") Pageable pageable) {
        Page<TransactionResponse> page = validationService.operationsEnAttente(pageable);
        return new PagedModel<>(page);
    }

    @PostMapping("/{id}/valider")
    public TransactionResponse valider(@PathVariable Long id,
            @Valid @RequestBody ValidationRequest request) {
        return validationService.traiter(id, request.decision());
    }
}
