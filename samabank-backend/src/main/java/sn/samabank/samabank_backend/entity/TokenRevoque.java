package sn.samabank.samabank_backend.entity;

import java.time.Instant;

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
 * Refresh token révoqué (logout). Identifié par son {@code jti}. Conservé
 * jusqu'à sa date d'expiration, après quoi il est purgé automatiquement
 * (inutile de bloquer un jeton déjà expiré).
 */
@Entity
@Table(name = "token_revoque")
@Getter
@Setter
@NoArgsConstructor
public class TokenRevoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String jti;

    @Column(name = "expire_le", nullable = false)
    private Instant expireLe;

    @Column(name = "revoque_le", nullable = false)
    private Instant revoqueLe;

    public TokenRevoque(String jti, Instant expireLe) {
        this.jti = jti;
        this.expireLe = expireLe;
        this.revoqueLe = Instant.now();
    }
}
