package sn.samabank.samabank_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.exception.CompteIntrouvableException;
import sn.samabank.samabank_backend.mapper.TransactionMapper;
import sn.samabank.samabank_backend.repository.CompteRepository;
import sn.samabank.samabank_backend.repository.TransactionRepository;

/**
 * Consultation de l'historique des transactions d'un compte (SEQ-04), paginé
 * (BNF-08). Un client ne peut consulter que ses propres comptes ; un conseiller
 * peut consulter n'importe quel compte.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> historique(Long compteId, String emailDemandeur,
            boolean accesTotal, Pageable pageable) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteIntrouvableException(compteId));

        if (!accesTotal && !compte.getClient().getEmail().equals(emailDemandeur)) {
            throw new AccessDeniedException("Ce compte ne vous appartient pas");
        }

        return transactionRepository.findHistorique(compteId, pageable)
                .map(transactionMapper::toResponse);
    }
}
