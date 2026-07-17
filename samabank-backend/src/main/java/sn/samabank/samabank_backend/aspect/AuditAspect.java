package sn.samabank.samabank_backend.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.samabank.samabank_backend.service.AuditService;

/**
 * Aspect d'audit (BNF-04) : après le retour réussi d'une méthode {@code @Auditable},
 * enregistre une entrée dans le journal (acteur, action, cible, valeur résultante,
 * adresse IP). La journalisation n'interfère jamais avec le métier.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final String SUFFIXE_DTO = "Response";

    private final AuditService auditService;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "resultat")
    public void journaliser(JoinPoint joinPoint, Auditable auditable, Object resultat) {
        try {
            String cibleType = auditable.cibleType().isBlank()
                    ? typeDepuisResultat(resultat)
                    : auditable.cibleType();
            auditService.journaliser(
                    acteurCourant(),
                    auditable.action(),
                    cibleType,
                    idDepuisResultat(resultat),
                    null,
                    resultat,
                    adresseIp());
        } catch (Exception ex) {
            log.warn("Audit non enregistré pour l'action {} : {}", auditable.action(), ex.getMessage());
        }
    }

    private String acteurCourant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null || !authentication.isAuthenticated())
                ? "anonyme"
                : authentication.getName();
    }

    private String adresseIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getRemoteAddr();
        } catch (IllegalStateException ex) {
            return null;
        }
    }

    private String typeDepuisResultat(Object resultat) {
        if (resultat == null) {
            return null;
        }
        String nom = resultat.getClass().getSimpleName();
        return nom.endsWith(SUFFIXE_DTO)
                ? nom.substring(0, nom.length() - SUFFIXE_DTO.length())
                : nom;
    }

    private String idDepuisResultat(Object resultat) {
        if (resultat == null) {
            return null;
        }
        for (String accesseur : new String[] {"id", "getId"}) {
            try {
                Method method = resultat.getClass().getMethod(accesseur);
                Object valeur = method.invoke(resultat);
                return valeur != null ? valeur.toString() : null;
            } catch (ReflectiveOperationException ignored) {
                // pas d'identifiant exploitable sur ce résultat
            }
        }
        return null;
    }
}
