package sn.samabank.samabank_backend.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.samabank.samabank_backend.entity.TokenRevoque;

public interface TokenRevoqueRepository extends JpaRepository<TokenRevoque, Long> {

    boolean existsByJti(String jti);

    long deleteByExpireLeBefore(Instant instant);
}
