package sn.samabank.samabank_backend.dto;

/**
 * Réponse d'authentification (allégée). L'email et les rôles ne sont pas
 * dupliqués ici : ils sont déjà encodés dans l'access token (claims {@code sub}
 * et {@code roles}).
 *
 * @param expiresIn durée de validité de l'access token, en secondes
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn) {

    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds);
    }
}
