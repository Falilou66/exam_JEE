package sn.samabank.samabank_backend.mapper;

import org.mapstruct.Mapper;

import sn.samabank.samabank_backend.dto.TransactionResponse;
import sn.samabank.samabank_backend.entity.Depot;
import sn.samabank.samabank_backend.entity.Retrait;
import sn.samabank.samabank_backend.entity.Transaction;
import sn.samabank.samabank_backend.entity.Virement;
import sn.samabank.samabank_backend.enums.Canal;

/**
 * Conversion Transaction (polymorphe) → DTO. Doit être appelée dans un contexte
 * transactionnel (accès aux comptes en LAZY).
 */
@Mapper
public interface TransactionMapper {

    default TransactionResponse toResponse(Transaction transaction) {
        String compteDestination = null;
        String motif = null;
        Canal canal = null;

        if (transaction instanceof Virement virement) {
            compteDestination = virement.getCompteDestination().getNumeroCompte();
            motif = virement.getMotif();
        } else if (transaction instanceof Depot depot) {
            canal = depot.getCanal();
        } else if (transaction instanceof Retrait retrait) {
            canal = retrait.getCanal();
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getMontant(),
                transaction.getStatut(),
                transaction.getLibelle(),
                transaction.getDateOperation(),
                transaction.getCompte().getNumeroCompte(),
                compteDestination,
                motif,
                canal);
    }
}
