package sn.samabank.samabank_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.dto.CreationUtilisateurRequest;
import sn.samabank.samabank_backend.dto.UtilisateurResponse;
import sn.samabank.samabank_backend.entity.Administrateur;
import sn.samabank.samabank_backend.entity.Conseiller;
import sn.samabank.samabank_backend.entity.Role;
import sn.samabank.samabank_backend.entity.Utilisateur;
import sn.samabank.samabank_backend.enums.TypeUtilisateur;
import sn.samabank.samabank_backend.exception.DonneeUniqueException;
import sn.samabank.samabank_backend.mapper.UtilisateurMapper;
import sn.samabank.samabank_backend.repository.RoleRepository;
import sn.samabank.samabank_backend.repository.UtilisateurRepository;

/**
 * Gestion des utilisateurs internes par l'administrateur (SEQ-10) : création de
 * conseillers ou d'administrateurs, avec le rôle correspondant. Journalisé.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ROLE_CONSEILLER = "ROLE_CONSEILLER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurMapper utilisateurMapper;

    @Auditable(action = "CREATION_USER", cibleType = "Utilisateur")
    @Transactional
    public UtilisateurResponse creer(CreationUtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new DonneeUniqueException("email", request.email());
        }

        Utilisateur utilisateur = construireSelonType(request);
        utilisateur.setEmail(request.email());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setActif(true);
        utilisateur.ajouterRole(roleDe(request.type()));

        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    private Utilisateur construireSelonType(CreationUtilisateurRequest request) {
        return switch (request.type()) {
            case CONSEILLER -> {
                Conseiller conseiller = new Conseiller();
                conseiller.setMatricule(request.matricule());
                conseiller.setAgence(request.agence());
                yield conseiller;
            }
            case ADMIN -> {
                Administrateur administrateur = new Administrateur();
                administrateur.setNiveauAcces(request.niveauAcces() != null ? request.niveauAcces() : 1);
                yield administrateur;
            }
        };
    }

    private Role roleDe(TypeUtilisateur type) {
        String libelle = type == TypeUtilisateur.CONSEILLER ? ROLE_CONSEILLER : ROLE_ADMIN;
        return roleRepository.findByLibelle(libelle)
                .orElseThrow(() -> new IllegalStateException("Rôle absent : " + libelle));
    }
}
