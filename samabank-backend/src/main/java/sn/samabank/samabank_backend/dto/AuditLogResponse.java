package sn.samabank.samabank_backend.dto;

import java.time.Instant;

/**
 * Vue d'une entrée du journal d'audit.
 */
public record AuditLogResponse(
        Long id,
        String acteur,
        String action,
        String cibleType,
        String cibleId,
        String valeurAvant,
        String valeurApres,
        Instant horodatage,
        String adresseIp) {
}
