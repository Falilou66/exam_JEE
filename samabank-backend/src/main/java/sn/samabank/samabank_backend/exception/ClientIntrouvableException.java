package sn.samabank.samabank_backend.exception;

/**
 * Client demandé introuvable (→ HTTP 404).
 */
public class ClientIntrouvableException extends ResourceNotFoundException {

    public ClientIntrouvableException(Long id) {
        super("CLIENT_INTROUVABLE", "Aucun client avec l'identifiant : " + id);
    }
}
