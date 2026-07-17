package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Violation d'une contrainte d'unicité métier (RG-8 : CNI/email uniques).
 * Renvoyée en HTTP 409 (Conflict).
 */
public class DonneeUniqueException extends BusinessException {

    public DonneeUniqueException(String champ, String valeur) {
        super(HttpStatus.CONFLICT, "DONNEE_DUPLIQUEE",
                "La valeur « %s » est déjà utilisée pour le champ %s".formatted(valeur, champ));
    }
}
