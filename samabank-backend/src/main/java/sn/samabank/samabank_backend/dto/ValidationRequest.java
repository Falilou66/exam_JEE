package sn.samabank.samabank_backend.dto;

import jakarta.validation.constraints.NotNull;

import sn.samabank.samabank_backend.enums.DecisionValidation;

/**
 * Décision de validation d'une opération sensible (SEQ-08).
 */
public record ValidationRequest(

        @NotNull(message = "La décision est obligatoire (APPROUVER ou REJETER)")
        DecisionValidation decision) {
}
