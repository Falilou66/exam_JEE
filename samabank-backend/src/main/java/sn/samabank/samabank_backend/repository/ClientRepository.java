package sn.samabank.samabank_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.samabank.samabank_backend.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    boolean existsByCni(String cni);

    boolean existsByNumeroClient(String numeroClient);
}
