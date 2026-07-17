package sn.samabank.samabank_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.samabank.samabank_backend.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);
}
