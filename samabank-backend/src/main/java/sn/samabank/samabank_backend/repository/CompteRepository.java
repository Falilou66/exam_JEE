package sn.samabank.samabank_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.samabank.samabank_backend.entity.Compte;

public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByNumeroCompte(String numeroCompte);

    boolean existsByNumeroCompte(String numeroCompte);

    List<Compte> findByClientId(Long clientId);

    List<Compte> findByClientEmail(String email);
}
