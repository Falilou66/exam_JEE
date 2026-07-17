package sn.samabank.samabank_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.service.TransactionService;

/**
 * Historique paginé des transactions d'un compte (SEQ-04, BF-04, BNF-08).
 */
@RestController
@RequestMapping("/api/v1/comptes")
@RequiredArgsConstructor
public class TransactionController {

    private static final String ROLE_CONSEILLER = "ROLE_CONSEILLER";

    private final TransactionService transactionService;

    @GetMapping("/{compteId}/transactions")
    @PreAuthorize("hasAnyRole('CLIENT', 'CONSEILLER')")
    public PagedModel<TransactionResponse> historique(
            @PathVariable Long compteId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {

        boolean estConseiller = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_CONSEILLER::equals);

        Page<TransactionResponse> page = transactionService.historique(
                compteId, authentication.getName(), estConseiller, pageable);
        return new PagedModel<>(page);
    }
}
