package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Opération invalide sur une transaction (ex. valider une transaction déjà
 * traitée, ou non soumise à validation). HTTP 409.
 */
public class EtatTransactionException extends BusinessException {

    public EtatTransactionException(String message) {
        super(HttpStatus.CONFLICT, "ETAT_TRANSACTION_INVALIDE", message);
    }
}
