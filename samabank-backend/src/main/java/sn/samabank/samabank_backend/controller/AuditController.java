package sn.samabank.samabank_backend.controller;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.AuditLogResponse;
import sn.samabank.samabank_backend.mapper.AuditLogMapper;
import sn.samabank.samabank_backend.service.AuditService;

/**
 * Consultation du journal d'audit (SEQ-11, BF-14) — réservé à l'administrateur,
 * en lecture seule et paginé, avec filtres optionnels.
 */
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    public PagedModel<AuditLogResponse> consulter(
            @RequestParam(required = false) String acteur,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFin,
            @PageableDefault(size = 20, sort = "horodatage", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLogResponse> page = auditService
                .rechercher(acteur, action, dateDebut, dateFin, pageable)
                .map(auditLogMapper::toResponse);
        return new PagedModel<>(page);
    }
}
