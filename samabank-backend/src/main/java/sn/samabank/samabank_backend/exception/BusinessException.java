package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Exception métier de base. Chaque règle de gestion violée lève une sous-classe
 * portant son propre statut HTTP et un code stable, exploités de façon
 * centralisée par {@code GlobalExceptionHandler} (BNF-07).
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
