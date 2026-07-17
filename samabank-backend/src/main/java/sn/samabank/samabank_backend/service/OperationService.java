package sn.samabank.samabank_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.dto.OperationRequest;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.entity.Depot;
import sn.samabank.samabank_backend.entity.Retrait;
import sn.samabank.samabank_backend.entity.Transaction;
import sn.samabank.samabank_backend.enums.Canal;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.exception.CompteIntrouvableException;
import sn.samabank.samabank_backend.mapper.TransactionMapper;
import sn.samabank.samabank_backend.repository.CompteRepository;
import sn.samabank.samabank_backend.repository.TransactionRepository;
import sn.samabank.samabank_backend.util.GenerateurNumero;

/**
 * Opérations de caisse (dépôt / retrait) réalisées par un conseiller. Réutilise
 * les mouvements de {@link Compte} (RG-3, RG-6). Permet notamment d'alimenter un
 * compte avant un virement.
 */
@Service
@RequiredArgsConstructor
public class OperationService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final GenerateurNumero generateurNumero;

    @Auditable(action = "DEPOT", cibleType = "Depot")
    @Transactional
    public TransactionResponse deposer(Long compteId, OperationRequest request) {
        Compte compte = getCompte(compteId);
        compte.crediter(request.montant());

        Depot depot = new Depot();
        depot.setCanal(canalOuDefaut(request.canal()));
        initTransaction(depot, compte, request, "Dépôt");
        return transactionMapper.toResponse(transactionRepository.save(depot));
    }

    @Auditable(action = "RETRAIT", cibleType = "Retrait")
    @Transactional
    public TransactionResponse retirer(Long compteId, OperationRequest request) {
        Compte compte = getCompte(compteId);
        compte.debiter(request.montant());

        Retrait retrait = new Retrait();
        retrait.setCanal(canalOuDefaut(request.canal()));
        initTransaction(retrait, compte, request, "Retrait");
        return transactionMapper.toResponse(transactionRepository.save(retrait));
    }

    private void initTransaction(Transaction transaction, Compte compte,
            OperationRequest request, String libelleParDefaut) {
        transaction.setReference(
                generateurNumero.genererReference(transactionRepository::existsByReference));
        transaction.setMontant(request.montant());
        transaction.setCompte(compte);
        transaction.setStatut(StatutTransaction.VALIDEE);
        transaction.setLibelle(
                request.libelle() != null ? request.libelle() : libelleParDefaut);
    }

    private Compte getCompte(Long compteId) {
        return compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteIntrouvableException(compteId));
    }

    private Canal canalOuDefaut(Canal canal) {
        return canal != null ? canal : Canal.AGENCE;
    }
}
