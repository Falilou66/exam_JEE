package sn.samabank.samabank_backend.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rôle applicatif (RBAC). Le libellé porte le préfixe Spring Security
 * ({@code ROLE_CLIENT}, {@code ROLE_CONSEILLER}, {@code ROLE_ADMIN}) afin d'être
 * mappé directement sur une autorité.
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String libelle;

    public Role(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role role)) {
            return false;
        }
        return libelle != null && libelle.equals(role.libelle);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(libelle);
    }
}
