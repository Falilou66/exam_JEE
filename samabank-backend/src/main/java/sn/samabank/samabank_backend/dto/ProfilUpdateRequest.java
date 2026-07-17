package sn.samabank.samabank_backend.dto;

import jakarta.validation.constraints.Size;

/**
 * Mise à jour du profil par le client (SEQ-12). Tous les champs sont optionnels ;
 * le mot de passe n'est changé que s'il est fourni.
 */
public record ProfilUpdateRequest(

        String telephone,

        String adresse,

        @Size(min = 6, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
        String nouveauMotDePasse) {
}
