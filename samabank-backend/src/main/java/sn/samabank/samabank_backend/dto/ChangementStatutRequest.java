package sn.samabank.samabank_backend.dto;

import jakarta.validation.constraints.NotNull;

import sn.samabank.samabank_backend.enums.StatutCompte;

/**
 * Changement d'état d'un compte (SEQ-09) : cible ACTIF (débloquer), BLOQUE
 * (bloquer) ou CLOTURE (clôturer).
 */
public record ChangementStatutRequest(

        @NotNull(message = "Le statut cible est obligatoire")
        StatutCompte statut) {
}
