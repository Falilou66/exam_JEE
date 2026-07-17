package sn.samabank.samabank_backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entrée du journal d'audit (BNF-04/05). Table alimentée uniquement par
 * l'aspect d'audit et jamais modifiée ni supprimée par l'application
 * (append-only). Consultable seulement par un administrateur.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String acteur;

    @Column(nullable = false, length = 60)
    private String action;

    @Column(name = "cible_type", length = 60)
    private String cibleType;

    @Column(name = "cible_id", length = 60)
    private String cibleId;

    @Lob
    @Column(name = "valeur_avant", columnDefinition = "TEXT")
    private String valeurAvant;

    @Lob
    @Column(name = "valeur_apres", columnDefinition = "TEXT")
    private String valeurApres;

    @Column(nullable = false)
    private Instant horodatage;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;
}
