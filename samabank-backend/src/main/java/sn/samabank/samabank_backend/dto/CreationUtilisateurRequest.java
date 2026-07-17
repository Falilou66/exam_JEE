package sn.samabank.samabank_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import sn.samabank.samabank_backend.enums.TypeUtilisateur;

/**
 * Création d'un utilisateur interne par l'administrateur (SEQ-10). Selon le
 * type : matricule/agence (conseiller) ou niveauAcces (admin).
 */
public record CreationUtilisateurRequest(

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String motDePasse,

        @NotNull(message = "Le type d'utilisateur est obligatoire")
        TypeUtilisateur type,

        String matricule,

        String agence,

        Integer niveauAcces) {
}
