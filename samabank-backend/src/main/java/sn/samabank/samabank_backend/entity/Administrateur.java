package sn.samabank.samabank_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Administrateur : supervise (gère utilisateurs & rôles, consulte l'audit).
 * Sous-type de {@link Utilisateur} (table jointe {@code administrateur}).
 */
@Entity
@Table(name = "administrateur")
@Getter
@Setter
@NoArgsConstructor
public class Administrateur extends Utilisateur {

    @Column(name = "niveau_acces", nullable = false)
    private int niveauAcces;
}
