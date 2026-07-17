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
 * Compte courant : autorise un découvert. Le solde disponible intègre donc le
 * découvert autorisé (RG-3).
 */
@Entity
@Table(name = "compte_courant")
@Getter
@Setter
@NoArgsConstructor
public class CompteCourant extends Compte {

    @Column(name = "decouvert_autorise", nullable = false, precision = 19, scale = 2)
    private BigDecimal decouvertAutorise = BigDecimal.ZERO;

    @Override
    public TypeCompte getType() {
        return TypeCompte.COURANT;
    }

    @Override
    public BigDecimal soldeDisponible() {
        return getSolde().add(decouvertAutorise);
    }
}
