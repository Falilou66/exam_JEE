package sn.samabank.samabank_backend.enums;

/**
 * Cycle de vie d'un compte (cf. diagramme d'états).
 * EN_CREATION → ACTIF ↔ BLOQUE → CLOTURE.
 */
public enum StatutCompte {
    EN_CREATION,
    ACTIF,
    BLOQUE,
    CLOTURE
}
