package sn.samabank.samabank_backend.controller;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.service.ReleveService;

/**
 * Téléchargement du relevé de compte PDF (SEQ-05, BF-05).
 */
@RestController
@RequestMapping("/api/v1/comptes")
@RequiredArgsConstructor
public class ReleveController {

    private static final String ROLE_CONSEILLER = "ROLE_CONSEILLER";

    private final ReleveService releveService;

    @GetMapping("/{compteId}/releve")
    @PreAuthorize("hasAnyRole('CLIENT', 'CONSEILLER')")
    public ResponseEntity<byte[]> telecharger(
            @PathVariable Long compteId,
            @RequestParam String mois,
            Authentication authentication) {

        YearMonth periode = parseMois(mois);
        boolean accesTotal = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_CONSEILLER::equals);

        byte[] pdf = releveService.genererReleve(
                compteId, periode, authentication.getName(), accesTotal);

        String fichier = "releve-%d-%s.pdf".formatted(compteId, mois);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fichier + "\"")
                .body(pdf);
    }

    private YearMonth parseMois(String mois) {
        try {
            return YearMonth.parse(mois);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Le paramètre 'mois' doit être au format AAAA-MM");
        }
    }
}
