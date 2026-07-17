package sn.samabank.samabank_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Vue d'un utilisateur interne (jamais le mot de passe).
 */
public record UtilisateurResponse(
        Long id,
        String email,
        String type,
        List<String> roles,
        boolean actif,
        LocalDateTime dateCreation) {
}
