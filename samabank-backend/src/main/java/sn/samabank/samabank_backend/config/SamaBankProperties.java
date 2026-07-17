package sn.samabank.samabank_backend.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration de l'application, liées au préfixe {@code samabank}
 * dans application.yml. Externalise notamment le secret JWT et le seuil de
 * validation des virements (RG-4), plutôt que de les coder en dur.
 */
@ConfigurationProperties(prefix = "samabank")
public record SamaBankProperties(Security security, Banque banque) {

    public record Security(Jwt jwt) {
        public record Jwt(String secret, long accessExpirationMs, long refreshExpirationMs, String issuer) {
        }
    }

    public record Banque(BigDecimal seuilValidationVirement, String deviseParDefaut) {
    }
}
