package sn.samabank.samabank_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Solde disponible insuffisant pour l'opération (RG-3). HTTP 422
 * (requête bien formée mais non traitable en l'état).
 */
public class SoldeInsuffisantException extends BusinessException {

    public SoldeInsuffisantException(String numeroCompte) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "SOLDE_INSUFFISANT",
                "Solde disponible insuffisant sur le compte " + numeroCompte);
    }
}
