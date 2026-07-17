package sn.samabank.samabank_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.samabank.samabank_backend.enums.TypeTransaction;

/**
 * Virement entre deux comptes. Le compte source est le {@code compte} hérité ;
 * le compte destination est spécifique. C'est l'opération centrale du système
 * (SEQ-03), exécutée de façon atomique par {@code VirementService}.
 */
@Entity
@Table(name = "virement")
@Getter
@Setter
@NoArgsConstructor
public class Virement extends Transaction {

    @Column(length = 255)
    private String motif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_destination_id", nullable = false)
    private Compte compteDestination;

    @Override
    public TypeTransaction getType() {
        return TypeTransaction.VIREMENT;
    }

    /** Alias sémantique : le compte source est le compte concerné hérité. */
    public Compte getCompteSource() {
        return getCompte();
    }

    public void setCompteSource(Compte compteSource) {
        setCompte(compteSource);
    }
}
