package sn.samabank.samabank_backend.service;

import java.math.BigDecimal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.config.SamaBankProperties;
import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.dto.VirementRequest;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.entity.Virement;
import sn.samabank.samabank_backend.enums.StatutCompte;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.exception.CompteBloqueException;
import sn.samabank.samabank_backend.exception.CompteIntrouvableException;
import sn.samabank.samabank_backend.exception.SoldeInsuffisantException;
import sn.samabank.samabank_backend.mapper.TransactionMapper;
import sn.samabank.samabank_backend.repository.CompteRepository;
import sn.samabank.samabank_backend.repository.TransactionRepository;
import sn.samabank.samabank_backend.util.GenerateurNumero;

/**
 * Cœur transactionnel (SEQ-03). Le virement est atomique ({@code @Transactional})
 * : débit du source et crédit de la destination forment une unité indivisible.
 * Applique RG-2 (source actif), RG-3 (solde suffisant), RG-4 (seuil de
 * validation), RG-6 (compte non actif refusé).
 */
@Service
@RequiredArgsConstructor
public class VirementService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final GenerateurNumero generateurNumero;
    private final SamaBankProperties properties;

    @Auditable(action = "VIREMENT", cibleType = "Virement")
    @Transactional
    public TransactionResponse effectuer(VirementRequest request, String emailInitiateur) {
        Compte source = compteRepository.findByNumeroCompte(request.compteSource())
                .orElseThrow(() -> new CompteIntrouvableException(request.compteSource()));
        Compte destination = compteRepository.findByNumeroCompte(request.compteDestination())
                .orElseThrow(() -> new CompteIntrouvableException(request.compteDestination()));

        verifierProprietaire(source, emailInitiateur);

        // RG-2 : le compte source doit être actif
        if (source.getStatut() != StatutCompte.ACTIF) {
            throw new CompteBloqueException(source.getNumeroCompte(), source.getStatut());
        }
        // RG-3 : solde disponible suffisant
        if (source.soldeDisponible().compareTo(request.montant()) < 0) {
            throw new SoldeInsuffisantException(source.getNumeroCompte());
        }

        Virement virement = new Virement();
        virement.setReference(generateurNumero.genererReference(transactionRepository::existsByReference));
        virement.setMontant(request.montant());
        virement.setMotif(request.motif());
        virement.setLibelle("Virement vers " + destination.getNumeroCompte());
        virement.setCompteSource(source);
        virement.setCompteDestination(destination);

        // RG-4 : au-delà du seuil, mise en attente sans mouvementer les comptes
        if (request.montant().compareTo(seuilValidation()) > 0) {
            virement.setStatut(StatutTransaction.EN_ATTENTE_VALIDATION);
            return transactionMapper.toResponse(transactionRepository.save(virement));
        }

        // Cas nominal : débit + crédit atomiques
        source.debiter(request.montant());
        destination.crediter(request.montant());
        virement.setStatut(StatutTransaction.VALIDEE);
        return transactionMapper.toResponse(transactionRepository.save(virement));
    }

    private void verifierProprietaire(Compte source, String emailInitiateur) {
        if (!source.getClient().getEmail().equals(emailInitiateur)) {
            throw new AccessDeniedException("Le compte source ne vous appartient pas");
        }
    }

    private BigDecimal seuilValidation() {
        return properties.banque().seuilValidationVirement();
    }
}
