package sn.samabank.samabank_backend.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import sn.samabank.samabank_backend.config.SamaBankProperties;

/**
 * Génération et validation des jetons JWT (HMAC-SHA). Deux types de jetons,
 * distingués par le claim {@code type} :
 * <ul>
 *   <li><b>access</b> — courte durée, présenté à chaque requête ;</li>
 *   <li><b>refresh</b> — longue durée, doté d'un identifiant unique ({@code jti})
 *       pour permettre sa révocation (logout), sert uniquement à obtenir un
 *       nouvel access token.</li>
 * </ul>
 * Le secret et les durées sont externalisés dans {@link SamaBankProperties}.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;
    private final String issuer;

    public JwtService(SamaBankProperties properties) {
        SamaBankProperties.Security.Jwt jwt = properties.security().jwt();
        this.key = Keys.hmacShaKeyFor(jwt.secret().getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = jwt.accessExpirationMs();
        this.refreshExpirationMs = jwt.refreshExpirationMs();
        this.issuer = jwt.issuer();
    }

    public String generateAccessToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Instant now = Instant.now();
        return baseBuilder(userDetails.getUsername(), now, accessExpirationMs)
                .claims(Map.of(CLAIM_TYPE, TYPE_ACCESS, CLAIM_ROLES, roles))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Instant now = Instant.now();
        return baseBuilder(userDetails.getUsername(), now, refreshExpirationMs)
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .signWith(key)
                .compact();
    }

    /** Valide un access token pour l'utilisateur donné (signature, expiration, type). */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE))
                    && claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Vérifie qu'il s'agit d'un refresh token valide et renvoie ses claims
     * (dont {@code sub} = email et {@code jti} = identifiant du jeton). Lève une
     * {@link JwtException} si le jeton est invalide, expiré ou n'est pas de type
     * refresh.
     */
    public Claims parseRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE))) {
            throw new JwtException("Le jeton fourni n'est pas un refresh token");
        }
        return claims;
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    private io.jsonwebtoken.JwtBuilder baseBuilder(String subject, Instant now, long ttlMs) {
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ttlMs)));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
