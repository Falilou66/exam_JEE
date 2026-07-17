package sn.samabank.samabank_backend.exception;

/**
 * Transaction demandée introuvable (→ HTTP 404).
 */
public class TransactionIntrouvableException extends ResourceNotFoundException {

    public TransactionIntrouvableException(Long id) {
        super("TRANSACTION_INTROUVABLE", "Aucune transaction avec l'identifiant : " + id);
    }
}
