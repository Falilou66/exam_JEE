package sn.samabank.samabank_backend.exception;

/**
 * Compte demandé introuvable (par identifiant ou numéro) (→ HTTP 404).
 */
public class CompteIntrouvableException extends ResourceNotFoundException {

    public CompteIntrouvableException(String reference) {
        super("COMPTE_INTROUVABLE", "Aucun compte pour la référence : " + reference);
    }

    public CompteIntrouvableException(Long id) {
        this("id=" + id);
    }
}
