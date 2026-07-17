package sn.samabank.samabank_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conseiller : gère (crée clients, ouvre comptes, valide les opérations
 * sensibles, bloque/débloque). Sous-type de {@link Utilisateur}.
 */
@Entity
@Table(name = "conseiller")
@Getter
@Setter
@NoArgsConstructor
public class Conseiller extends Utilisateur {

    @Column(unique = true, length = 50)
    private String matricule;

    @Column(length = 100)
    private String agence;
}
