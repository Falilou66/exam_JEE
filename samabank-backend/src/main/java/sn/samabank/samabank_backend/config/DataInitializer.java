package sn.samabank.samabank_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.samabank.samabank_backend.entity.Administrateur;
import sn.samabank.samabank_backend.entity.Conseiller;
import sn.samabank.samabank_backend.entity.Role;
import sn.samabank.samabank_backend.repository.RoleRepository;
import sn.samabank.samabank_backend.repository.UtilisateurRepository;

/**
 * Amorce les données minimales au démarrage : les trois rôles RBAC et un
 * administrateur par défaut (remplace le seed Flyway). Idempotent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CONSEILLER = "ROLE_CONSEILLER";
    public static final String ROLE_CLIENT = "ROLE_CLIENT";

    private static final String ADMIN_EMAIL = "admin@samabank.sn";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String CONSEILLER_EMAIL = "conseiller@samabank.sn";
    private static final String CONSEILLER_PASSWORD = "Conseiller@123";

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role admin = ensureRole(ROLE_ADMIN);
        Role conseiller = ensureRole(ROLE_CONSEILLER);
        ensureRole(ROLE_CLIENT);

        if (!utilisateurRepository.existsByEmail(ADMIN_EMAIL)) {
            Administrateur administrateur = new Administrateur();
            administrateur.setEmail(ADMIN_EMAIL);
            administrateur.setMotDePasse(passwordEncoder.encode(ADMIN_PASSWORD));
            administrateur.setActif(true);
            administrateur.setNiveauAcces(1);
            administrateur.ajouterRole(admin);
            utilisateurRepository.save(administrateur);
            log.info("Administrateur par défaut créé : {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
        }

        if (!utilisateurRepository.existsByEmail(CONSEILLER_EMAIL)) {
            Conseiller c = new Conseiller();
            c.setEmail(CONSEILLER_EMAIL);
            c.setMotDePasse(passwordEncoder.encode(CONSEILLER_PASSWORD));
            c.setActif(true);
            c.setMatricule("CONS-001");
            c.setAgence("Agence Centrale");
            c.ajouterRole(conseiller);
            utilisateurRepository.save(c);
            log.info("Conseiller par défaut créé : {} / {}", CONSEILLER_EMAIL, CONSEILLER_PASSWORD);
        }
    }

    private Role ensureRole(String libelle) {
        return roleRepository.findByLibelle(libelle)
                .orElseGet(() -> roleRepository.save(new Role(libelle)));
    }
}
