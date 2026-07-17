package sn.samabank.samabank_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Client de la banque : utilise ses comptes (consultation, virements). Sous-type
 * de {@link Utilisateur} — il possède donc aussi des identifiants de connexion
 * (rôle {@code ROLE_CLIENT}). Un client possède 0..* comptes (RG-1), navigués
 * via CompteRepository (association unidirectionnelle Compte → Client).
 */
@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client extends Utilisateur {

    @Column(name = "numero_client", unique = true, length = 20, updatable = false)
    private String numeroClient;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 30)
    private String cni;

    @Column(length = 30)
    private String telephone;

    @Column(length = 255)
    private String adresse;
}
