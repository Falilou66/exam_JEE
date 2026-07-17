package sn.samabank.samabank_backend.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marque une méthode de service comme opération sensible à journaliser
 * automatiquement (BNF-04). Interceptée par {@link AuditAspect}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /** Libellé de l'action (ex. VIREMENT, CREATION_CLIENT). */
    String action();

    /** Type de la cible (ex. Compte). Déduit du résultat si laissé vide. */
    String cibleType() default "";
}
