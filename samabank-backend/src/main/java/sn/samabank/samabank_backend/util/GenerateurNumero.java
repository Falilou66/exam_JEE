package sn.samabank.samabank_backend.util;

import java.security.SecureRandom;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

/**
 * Génère les identifiants métier uniques : numéro de client et numéro de compte
 * (RIB, RG-7). L'unicité est garantie en réessayant tant que le numéro existe
 * déjà (collision extrêmement improbable).
 */
@Component
public class GenerateurNumero {

    private static final String PREFIXE_RIB = "SN";
    private static final int LONGUEUR_RIB = 22;
    private static final String PREFIXE_CLIENT = "CLI";
    private static final int LONGUEUR_CLIENT = 9;
    private static final String PREFIXE_REFERENCE = "TX";
    private static final int LONGUEUR_REFERENCE = 14;

    private final SecureRandom random = new SecureRandom();

    /** RIB unique (RG-7), ex. SN + 22 chiffres. */
    public String genererNumeroCompte(Predicate<String> existeDeja) {
        return genererUnique(PREFIXE_RIB, LONGUEUR_RIB, existeDeja);
    }

    /** Numéro client unique, ex. CLI + 9 chiffres. */
    public String genererNumeroClient(Predicate<String> existeDeja) {
        return genererUnique(PREFIXE_CLIENT, LONGUEUR_CLIENT, existeDeja);
    }

    /** Référence de transaction unique, ex. TX + 14 chiffres. */
    public String genererReference(Predicate<String> existeDeja) {
        return genererUnique(PREFIXE_REFERENCE, LONGUEUR_REFERENCE, existeDeja);
    }

    private String genererUnique(String prefixe, int longueur, Predicate<String> existeDeja) {
        String numero;
        do {
            numero = genererNumero(prefixe, longueur);
        } while (existeDeja.test(numero));
        return numero;
    }

    private String genererNumero(String prefixe, int nbChiffres) {
        StringBuilder sb = new StringBuilder(prefixe);
        for (int i = 0; i < nbChiffres; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
