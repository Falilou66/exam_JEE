package sn.samabank.samabank_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import sn.samabank.samabank_backend.enums.StatutCompte;
import sn.samabank.samabank_backend.enums.TypeCompte;

/**
 * Vue d'un compte exposée par l'API.
 */
public record CompteResponse(
        Long id,
        String numeroCompte,
        TypeCompte type,
        BigDecimal solde,
        BigDecimal soldeDisponible,
        String devise,
        StatutCompte statut,
        LocalDate dateOuverture,
        Long clientId) {
}
