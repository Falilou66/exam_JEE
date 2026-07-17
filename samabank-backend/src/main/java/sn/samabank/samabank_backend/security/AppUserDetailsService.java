package sn.samabank.samabank_backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.entity.Utilisateur;
import sn.samabank.samabank_backend.repository.UtilisateurRepository;

/**
 * Charge un utilisateur par son email pour Spring Security. Utilisé à la fois
 * par le processus d'authentification (login) et par le filtre JWT.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun utilisateur pour l'email : " + email));
        return new SecurityUser(utilisateur);
    }
}
