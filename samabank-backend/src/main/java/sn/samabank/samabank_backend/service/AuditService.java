package sn.samabank.samabank_backend.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.samabank.samabank_backend.entity.AuditLog;
import sn.samabank.samabank_backend.repository.AuditLogRepository;
import tools.jackson.databind.ObjectMapper;

/**
 * Journalisation et consultation de l'audit (BNF-04/05). L'écriture se fait dans
 * une transaction indépendante ({@code REQUIRES_NEW}) pour que la trace soit
 * durable même si la transaction métier échoue par la suite. Aucune méthode de
 * modification/suppression n'est exposée (append-only).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void journaliser(String acteur, String action, String cibleType, String cibleId,
            Object valeurAvant, Object valeurApres, String adresseIp) {
        try {
            AuditLog entree = new AuditLog();
            entree.setActeur(acteur);
            entree.setAction(action);
            entree.setCibleType(cibleType);
            entree.setCibleId(cibleId);
            entree.setValeurAvant(toJson(valeurAvant));
            entree.setValeurApres(toJson(valeurApres));
            entree.setHorodatage(Instant.now());
            entree.setAdresseIp(adresseIp);
            auditLogRepository.save(entree);
        } catch (Exception ex) {
            // La journalisation ne doit jamais casser l'opération métier
            log.error("Échec de la journalisation d'audit (action={}) : {}", action, ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> rechercher(String acteur, String action,
            Instant debut, Instant fin, Pageable pageable) {
        return auditLogRepository.rechercher(
                videEnNull(acteur), videEnNull(action), debut, fin, pageable);
    }

    private String toJson(Object valeur) {
        if (valeur == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(valeur);
        } catch (Exception ex) {
            return String.valueOf(valeur);
        }
    }

    private String videEnNull(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur;
    }
}
