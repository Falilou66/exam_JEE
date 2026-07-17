package sn.samabank.samabank_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.config.SamaBankProperties;
import sn.samabank.samabank_backend.dto.CompteResponse;
import sn.samabank.samabank_backend.dto.OuvertureCompteRequest;
import sn.samabank.samabank_backend.entity.Client;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.entity.CompteCourant;
import sn.samabank.samabank_backend.entity.CompteEpargne;
import sn.samabank.samabank_backend.enums.StatutCompte;
import sn.samabank.samabank_backend.exception.ClientIntrouvableException;
import sn.samabank.samabank_backend.exception.CompteIntrouvableException;
import sn.samabank.samabank_backend.exception.EtatCompteException;
import sn.samabank.samabank_backend.mapper.CompteMapper;
import sn.samabank.samabank_backend.repository.ClientRepository;
import sn.samabank.samabank_backend.repository.CompteRepository;
import sn.samabank.samabank_backend.util.GenerateurNumero;

/**
 * Gestion des comptes : ouverture (SEQ-07) et changement d'état (SEQ-09).
 * Le RIB est généré automatiquement et immuable (RG-7).
 */
@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final CompteMapper compteMapper;
    private final GenerateurNumero generateurNumero;
    private final SamaBankProperties properties;

    @Auditable(action = "OUVERTURE_COMPTE", cibleType = "Compte")
    @Transactional
    public CompteResponse ouvrir(Long clientId, OuvertureCompteRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientIntrouvableException(clientId));

        Compte compte = creerSelonType(request);
        compte.setClient(client);
        compte.setDevise(deviseOuDefaut(request.devise()));
        compte.setStatut(StatutCompte.ACTIF);
        compte.setNumeroCompte(
                generateurNumero.genererNumeroCompte(compteRepository::existsByNumeroCompte));

        return compteMapper.toResponse(compteRepository.save(compte));
    }

    @Auditable(action = "CHANGEMENT_STATUT_COMPTE", cibleType = "Compte")
    @Transactional
    public CompteResponse changerStatut(Long compteId, StatutCompte cible) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteIntrouvableException(compteId));

        switch (cible) {
            case BLOQUE -> compte.bloquer();
            case ACTIF -> compte.debloquer();
            case CLOTURE -> compte.cloturer();
            default -> throw new EtatCompteException("Transition non autorisée vers l'état : " + cible);
        }
        // dirty checking : la modification est persistée à la fin de la transaction
        return compteMapper.toResponse(compte);
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> comptesDuClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ClientIntrouvableException(clientId);
        }
        return compteRepository.findByClientId(clientId).stream()
                .map(compteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> comptesParEmail(String email) {
        return compteRepository.findByClientEmail(email).stream()
                .map(compteMapper::toResponse)
                .toList();
    }

    private Compte creerSelonType(OuvertureCompteRequest request) {
        return switch (request.type()) {
            case COURANT -> {
                CompteCourant courant = new CompteCourant();
                courant.setDecouvertAutorise(valeurOuZero(request.decouvertAutorise()));
                yield courant;
            }
            case EPARGNE -> {
                CompteEpargne epargne = new CompteEpargne();
                epargne.setTauxInteret(valeurOuZero(request.tauxInteret()));
                yield epargne;
            }
        };
    }

    private String deviseOuDefaut(String devise) {
        return (devise == null || devise.isBlank())
                ? properties.banque().deviseParDefaut()
                : devise;
    }

    private BigDecimal valeurOuZero(BigDecimal valeur) {
        return valeur != null ? valeur : BigDecimal.ZERO;
    }
}
