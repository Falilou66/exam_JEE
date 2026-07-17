package sn.samabank.samabank_backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import sn.samabank.samabank_backend.dto.UtilisateurResponse;
import sn.samabank.samabank_backend.entity.Administrateur;
import sn.samabank.samabank_backend.entity.Client;
import sn.samabank.samabank_backend.entity.Conseiller;
import sn.samabank.samabank_backend.entity.Role;
import sn.samabank.samabank_backend.entity.Utilisateur;

/**
 * Conversion Utilisateur (polymorphe) → DTO, avec le type concret et les rôles.
 */
@Mapper
public interface UtilisateurMapper {

    default UtilisateurResponse toResponse(Utilisateur utilisateur) {
        List<String> roles = utilisateur.getRoles().stream()
                .map(Role::getLibelle)
                .sorted()
                .toList();
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getEmail(),
                typeDe(utilisateur),
                roles,
                utilisateur.isActif(),
                utilisateur.getDateCreation());
    }

    private String typeDe(Utilisateur utilisateur) {
        if (utilisateur instanceof Administrateur) {
            return "ADMIN";
        }
        if (utilisateur instanceof Conseiller) {
            return "CONSEILLER";
        }
        if (utilisateur instanceof Client) {
            return "CLIENT";
        }
        return "UTILISATEUR";
    }
}
