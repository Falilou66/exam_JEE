package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

import sn.samabank.samabank_backend.enums.StatutCompte;

/**
 * Opération refusée car le compte n'est pas actif (RG-2, RG-6). HTTP 409.
 */
public class CompteBloqueException extends BusinessException {

    public CompteBloqueException(String numeroCompte, StatutCompte statut) {
        super(HttpStatus.CONFLICT, "COMPTE_INACTIF",
                "Le compte %s n'est pas actif (état : %s)".formatted(numeroCompte, statut));
    }
}
