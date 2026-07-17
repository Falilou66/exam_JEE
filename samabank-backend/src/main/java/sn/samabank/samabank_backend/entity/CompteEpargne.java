package sn.samabank.samabank_backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.samabank.samabank_backend.enums.TypeCompte;

/**
 * Compte épargne : rémunéré par un taux d'intérêt, sans découvert. Le solde
 * disponible est donc strictement le solde.
 */
@Entity
@Table(name = "compte_epargne")
@Getter
@Setter
@NoArgsConstructor
public class CompteEpargne extends Compte {

    @Column(name = "taux_interet", nullable = false, precision = 5, scale = 4)
    private BigDecimal tauxInteret = BigDecimal.ZERO;

    @Override
    public TypeCompte getType() {
        return TypeCompte.EPARGNE;
    }

    @Override
    public BigDecimal soldeDisponible() {
        return getSolde();
    }

    /** Intérêts théoriques sur le solde courant. */
    public BigDecimal calculerInteret() {
        return getSolde().multiply(tauxInteret);
    }
}
