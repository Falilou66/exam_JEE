package sn.samabank.samabank_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.entity.Transaction;
import sn.samabank.samabank_backend.entity.Virement;
import sn.samabank.samabank_backend.enums.DecisionValidation;
import sn.samabank.samabank_backend.enums.StatutCompte;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.exception.CompteBloqueException;
import sn.samabank.samabank_backend.exception.EtatTransactionException;
import sn.samabank.samabank_backend.exception.SoldeInsuffisantException;
import sn.samabank.samabank_backend.exception.TransactionIntrouvableException;
import sn.samabank.samabank_backend.mapper.TransactionMapper;
import sn.samabank.samabank_backend.repository.TransactionRepository;

/**
 * Validation d'une opération sensible par un conseiller (SEQ-08). À l'approbation,
 * le solde est revérifié puis le virement est exécuté atomiquement ; au rejet, la
 * transaction est marquée REJETEE sans mouvement.
 */
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Auditable(action = "VALIDATION_OPERATION", cibleType = "Transaction")
    @Transactional(readOnly = true)
    public Page<TransactionResponse> operationsEnAttente(Pageable pageable) {
        return transactionRepository
                .findByStatut(StatutTransaction.EN_ATTENTE_VALIDATION, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional
    public TransactionResponse traiter(Long transactionId, DecisionValidation decision) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionIntrouvableException(transactionId));

        if (transaction.getStatut() != StatutTransaction.EN_ATTENTE_VALIDATION) {
            throw new EtatTransactionException("La transaction n'est pas en attente de validation");
        }
        if (!(transaction instanceof Virement virement)) {
            throw new EtatTransactionException("Seuls les virements sont soumis à validation");
        }

        if (decision == DecisionValidation.APPROUVER) {
            approuver(virement);
        } else {
            virement.setStatut(StatutTransaction.REJETEE);
        }
        return transactionMapper.toResponse(virement);
    }

    private void approuver(Virement virement) {
        Compte source = virement.getCompteSource();
        Compte destination = virement.getCompteDestination();

        // Revérification des règles au moment de l'approbation
        if (source.getStatut() != StatutCompte.ACTIF) {
            throw new CompteBloqueException(source.getNumeroCompte(), source.getStatut());
        }
        if (source.soldeDisponible().compareTo(virement.getMontant()) < 0) {
            throw new SoldeInsuffisantException(source.getNumeroCompte());
        }

        source.debiter(virement.getMontant());
        destination.crediter(virement.getMontant());
        virement.setStatut(StatutTransaction.VALIDEE);
    }
}
