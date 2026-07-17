package sn.samabank.samabank_backend.service;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.samabank.samabank_backend.entity.TokenRevoque;
import sn.samabank.samabank_backend.repository.TokenRevoqueRepository;

/**
 * Denylist des refresh tokens révoqués (logout). Un refresh token révoqué ne
 * peut plus renouveler d'access token. Les entrées expirées sont purgées
 * périodiquement (un jeton déjà expiré n'a plus besoin d'être bloqué).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private final TokenRevoqueRepository tokenRevoqueRepository;

    @Transactional
    public void revoquer(String jti, Instant expireLe) {
        if (jti != null && !tokenRevoqueRepository.existsByJti(jti)) {
            tokenRevoqueRepository.save(new TokenRevoque(jti, expireLe));
        }
    }

    @Transactional(readOnly = true)
    public boolean estRevoque(String jti) {
        return jti != null && tokenRevoqueRepository.existsByJti(jti);
    }

    /** Purge horaire des jetons révoqués déjà expirés. */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void purgerExpires() {
        long supprimes = tokenRevoqueRepository.deleteByExpireLeBefore(Instant.now());
        if (supprimes > 0) {
            log.debug("Denylist : {} refresh token(s) expiré(s) purgé(s)", supprimes);
        }
    }
}
