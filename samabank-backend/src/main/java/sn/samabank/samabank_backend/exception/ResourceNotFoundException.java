package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Ressource demandée introuvable (→ HTTP 404). Les exceptions plus spécifiques
 * (ex. CompteIntrouvableException) pourront étendre celle-ci.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }

    protected ResourceNotFoundException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
