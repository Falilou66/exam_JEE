package sn.samabank.samabank_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Demande de virement (SEQ-03). Les comptes sont désignés par leur numéro (RIB).
 */
public record VirementRequest(

        @NotBlank(message = "Le compte source est obligatoire")
        String compteSource,

        @NotBlank(message = "Le compte destination est obligatoire")
        String compteDestination,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        BigDecimal montant,

        String motif) {
}
