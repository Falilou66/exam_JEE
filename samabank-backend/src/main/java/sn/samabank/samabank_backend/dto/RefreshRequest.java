package sn.samabank.samabank_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Corps de la requête de renouvellement (POST /api/v1/auth/refresh).
 */
public record RefreshRequest(

        @NotBlank(message = "Le refresh token est obligatoire")
        String refreshToken) {
}
