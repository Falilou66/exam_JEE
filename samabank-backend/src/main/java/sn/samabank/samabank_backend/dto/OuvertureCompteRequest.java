package sn.samabank.samabank_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import sn.samabank.samabank_backend.enums.TypeCompte;

/**
 * Données d'ouverture d'un compte (SEQ-07). Selon le type, {@code decouvertAutorise}
 * (courant) ou {@code tauxInteret} (épargne) sont exploités ; la devise est
 * optionnelle (valeur par défaut sinon).
 */
public record OuvertureCompteRequest(

        @NotNull(message = "Le type de compte est obligatoire")
        TypeCompte type,

        String devise,

        @DecimalMin(value = "0.0", message = "Le découvert autorisé doit être positif")
        BigDecimal decouvertAutorise,

        @DecimalMin(value = "0.0", message = "Le taux d'intérêt doit être positif")
        BigDecimal tauxInteret) {
}
