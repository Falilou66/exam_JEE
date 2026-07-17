package sn.samabank.samabank_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.samabank.samabank_backend.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByLibelle(String libelle);
}
