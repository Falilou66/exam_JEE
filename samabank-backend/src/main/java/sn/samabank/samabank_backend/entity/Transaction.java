package sn.samabank.samabank_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import sn.samabank.samabank_backend.enums.StatutTransaction;
import sn.samabank.samabank_backend.enums.TypeTransaction;

/**
 * Transaction bancaire (classe mère abstraite, héritage JPA {@code JOINED}).
 * Le champ {@code compte} désigne le compte concerné (le compte source pour un
 * virement). Table nommée {@code transaction_bancaire} car « transaction » est
 * un mot réservé SQL.
 */
@Entity
@Table(name = "transaction_bancaire")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Transaction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String reference;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_operation", nullable = false, updatable = false)
    private LocalDateTime dateOperation;

    @Column(length = 255)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutTransaction statut = StatutTransaction.VALIDEE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @PrePersist
    protected void onCreate() {
        if (dateOperation == null) {
            dateOperation = LocalDateTime.now();
        }
    }

    /** Nature concrète de la transaction (implémentée par chaque sous-classe). */
    public abstract TypeTransaction getType();
}
