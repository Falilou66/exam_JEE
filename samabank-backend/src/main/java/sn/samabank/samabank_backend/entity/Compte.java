package sn.samabank.samabank_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import sn.samabank.samabank_backend.enums.StatutCompte;
import sn.samabank.samabank_backend.enums.TypeCompte;
import sn.samabank.samabank_backend.exception.CompteBloqueException;
import sn.samabank.samabank_backend.exception.EtatCompteException;
import sn.samabank.samabank_backend.exception.SoldeInsuffisantException;

/**
 * Compte bancaire (classe mère abstraite, héritage JPA {@code JOINED}).
 * Porte le solde, la devise, l'état et le lien vers le client propriétaire.
 * Les transitions d'état (RG-5, RG-6) sont encapsulées ici.
 */
@Entity
@Table(name = "compte")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Compte extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_compte", nullable = false, unique = true, updatable = false, length = 34)
    private String numeroCompte;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String devise;

    @Column(name = "date_ouverture", nullable = false, updatable = false)
    private LocalDate dateOuverture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCompte statut = StatutCompte.ACTIF;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false, updatable = false)
    private Client client;

    @PrePersist
    protected void onCreate() {
        if (dateOuverture == null) {
            dateOuverture = LocalDate.now();
        }
    }

    /** Type concret du compte (implémenté par chaque sous-classe). */
    public abstract TypeCompte getType();

    /** Solde réellement mobilisable (RG-3 : intègre le découvert pour un courant). */
    public abstract BigDecimal soldeDisponible();

    // --- Transitions d'état (diagramme d'états) ---

    /** RG-6 : blocage d'un compte actif. */
    public void bloquer() {
        if (statut != StatutCompte.ACTIF) {
            throw new EtatCompteException(
                    "Seul un compte ACTIF peut être bloqué (état courant : " + statut + ")");
        }
        this.statut = StatutCompte.BLOQUE;
    }

    /** Déblocage d'un compte bloqué. */
    public void debloquer() {
        if (statut != StatutCompte.BLOQUE) {
            throw new EtatCompteException(
                    "Seul un compte BLOQUE peut être débloqué (état courant : " + statut + ")");
        }
        this.statut = StatutCompte.ACTIF;
    }

    /** RG-5 : clôture possible uniquement si le solde est nul. */
    public void cloturer() {
        if (statut == StatutCompte.CLOTURE) {
            throw new EtatCompteException("Le compte est déjà clôturé");
        }
        if (solde.compareTo(BigDecimal.ZERO) != 0) {
            throw new EtatCompteException("Un compte ne peut être clôturé que si son solde est nul");
        }
        this.statut = StatutCompte.CLOTURE;
    }

    // --- Mouvements (RG-2, RG-3, RG-6) ---

    /** Crédite le compte. RG-6 : refusé si le compte n'est pas actif. */
    public void crediter(BigDecimal montant) {
        exigerActif();
        this.solde = this.solde.add(montant);
    }

    /**
     * Débite le compte. RG-6 : refusé si le compte n'est pas actif.
     * RG-3 : refusé si le solde disponible est insuffisant.
     */
    public void debiter(BigDecimal montant) {
        exigerActif();
        if (soldeDisponible().compareTo(montant) < 0) {
            throw new SoldeInsuffisantException(numeroCompte);
        }
        this.solde = this.solde.subtract(montant);
    }

    private void exigerActif() {
        if (statut != StatutCompte.ACTIF) {
            throw new CompteBloqueException(numeroCompte, statut);
        }
    }
}
