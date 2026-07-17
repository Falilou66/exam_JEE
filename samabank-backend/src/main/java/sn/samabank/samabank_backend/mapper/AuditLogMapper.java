package sn.samabank.samabank_backend.mapper;

import org.mapstruct.Mapper;

import sn.samabank.samabank_backend.dto.AuditLogResponse;
import sn.samabank.samabank_backend.entity.AuditLog;

/**
 * Conversion AuditLog → DTO.
 */
@Mapper
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);
}
