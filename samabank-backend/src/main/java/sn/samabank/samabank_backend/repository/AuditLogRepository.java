package sn.samabank.samabank_backend.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.samabank.samabank_backend.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** Recherche filtrée (tous les critères sont optionnels). */
    @Query("""
            select a from AuditLog a
            where (:acteur is null or a.acteur = :acteur)
              and (:action is null or a.action = :action)
              and (:debut  is null or a.horodatage >= :debut)
              and (:fin    is null or a.horodatage <= :fin)
            """)
    Page<AuditLog> rechercher(@Param("acteur") String acteur,
            @Param("action") String action,
            @Param("debut") Instant debut,
            @Param("fin") Instant fin,
            Pageable pageable);
}
