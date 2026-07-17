package sn.samabank.samabank_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import sn.samabank.samabank_backend.enums.Canal;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.enums.TypeTransaction;

/**
 * Vue d'une transaction exposée par l'API. Les champs {@code compteDestination}
 * (virement) et {@code canal} (dépôt/retrait) sont renseignés selon le type.
 */
public record TransactionResponse(
        Long id,
        String reference,
        TypeTransaction type,
        BigDecimal montant,
        StatutTransaction statut,
        String libelle,
        LocalDateTime dateOperation,
        String compte,
        String compteDestination,
        String motif,
        Canal canal) {
}
