package sn.samabank.samabank_backend.enums;

/**
 * Cycle de vie d'une transaction (cf. diagramme d'états).
 * montant &gt; seuil → EN_ATTENTE_VALIDATION → VALIDEE / REJETEE ;
 * montant ≤ seuil → VALIDEE directement.
 */
public enum StatutTransaction {
    EN_ATTENTE_VALIDATION,
    VALIDEE,
    REJETEE
}
