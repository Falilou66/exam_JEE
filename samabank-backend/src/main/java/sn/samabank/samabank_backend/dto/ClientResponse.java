package sn.samabank.samabank_backend.dto;

import java.time.LocalDateTime;

/**
 * Vue publique d'un client (jamais le mot de passe — BNF-10 séparation DTO/Entity).
 */
public record ClientResponse(
        Long id,
        String numeroClient,
        String nom,
        String prenom,
        String cni,
        String email,
        String telephone,
        String adresse,
        boolean actif,
        LocalDateTime dateCreation) {
}
