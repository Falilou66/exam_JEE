package sn.samabank.samabank_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.AuthResponse;
import sn.samabank.samabank_backend.dto.LoginRequest;
import sn.samabank.samabank_backend.dto.RefreshRequest;
import sn.samabank.samabank_backend.security.AppUserDetailsService;
import sn.samabank.samabank_backend.security.JwtService;

/**
 * Service d'authentification (SEQ-01). Vérifie les identifiants via le
 * {@link AuthenticationManager} (BCrypt) puis émet une paire access/refresh.
 * Le refresh est stateless (validé par signature) mais révocable : le logout
 * inscrit son {@code jti} dans une denylist consultée à chaque renouvellement.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;
    private final TokenRevocationService tokenRevocationService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse()));
        return genererTokens((UserDetails) authentication.getPrincipal());
    }

    public AuthResponse refresh(RefreshRequest request) {
        Claims claims = lireRefreshToken(request.refreshToken());
        if (tokenRevocationService.estRevoque(claims.getId())) {
            throw new BadCredentialsException("Refresh token révoqué (session terminée)");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return genererTokens(userDetails);
    }

    /**
     * Déconnexion : révoque le refresh token présenté. Idempotent — un jeton
     * déjà invalide ou expiré est simplement ignoré (pas d'erreur).
     */
    public void logout(RefreshRequest request) {
        try {
            Claims claims = jwtService.parseRefreshToken(request.refreshToken());
            tokenRevocationService.revoquer(claims.getId(), claims.getExpiration().toInstant());
        } catch (JwtException | IllegalArgumentException ex) {
            // jeton déjà invalide/expiré : rien à révoquer
        }
    }

    private Claims lireRefreshToken(String refreshToken) {
        try {
            return jwtService.parseRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("Refresh token invalide ou expiré");
        }
    }

    private AuthResponse genererTokens(UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        long expiresInSeconds = jwtService.getAccessExpirationMs() / 1000;
        return AuthResponse.of(accessToken, refreshToken, expiresInSeconds);
    }
}
