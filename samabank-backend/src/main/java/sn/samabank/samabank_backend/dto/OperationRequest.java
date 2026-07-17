package sn.samabank.samabank_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import sn.samabank.samabank_backend.enums.Canal;

/**
 * Opération de caisse (dépôt ou retrait) effectuée par un conseiller. Le canal
 * est optionnel (AGENCE par défaut).
 */
public record OperationRequest(

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        BigDecimal montant,

        Canal canal,

        String libelle) {
}
