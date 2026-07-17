package sn.samabank.samabank_backend.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestion centralisée des exceptions (BNF-07). Hérite de
 * {@link ResponseEntityExceptionHandler} pour bénéficier du traitement RFC 7807
 * des exceptions du framework (404, 405, 415…) et ajoute la gestion des
 * exceptions métier et de sécurité. Aucune stacktrace n'est exposée au client.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** Règles de gestion violées : statut et code portés par l'exception. */
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problem.setProperty("code", ex.getCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Paramètre/argument invalide côté client (→ 400). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setProperty("code", "ARGUMENT_INVALIDE");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Authentification manquante ou invalide (→ 401). */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Authentification requise ou invalide");
        problem.setProperty("code", "UNAUTHENTICATED");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Accès refusé par le contrôle de rôle @PreAuthorize (→ 403). */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Accès refusé : vous n'avez pas les droits requis");
        problem.setProperty("code", "ACCESS_DENIED");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Filet de sécurité : toute exception non prévue (→ 500), tracée côté serveur. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, WebRequest request) {
        log.error("Erreur inattendue sur {}", request.getDescription(false), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
        problem.setProperty("code", "INTERNAL_ERROR");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Validation des DTO (@Valid) : enrichit le ProblemDetail du framework. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> erreurs = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();

        ProblemDetail problem = ex.getBody();
        problem.setDetail("Un ou plusieurs champs sont invalides");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("erreurs", erreurs);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private String formatFieldError(FieldError error) {
        return "%s : %s".formatted(error.getField(), error.getDefaultMessage());
    }
}
