package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Transition d'état de compte invalide ou opération interdite dans l'état
 * courant (RG-5, RG-6). Renvoyée en HTTP 409 (Conflict).
 */
public class EtatCompteException extends BusinessException {

    public EtatCompteException(String message) {
        super(HttpStatus.CONFLICT, "ETAT_COMPTE_INVALIDE", message);
    }
}
