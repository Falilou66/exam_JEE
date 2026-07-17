package sn.samabank.samabank_backend.mapper;

import org.mapstruct.Mapper;

import sn.samabank.samabank_backend.dto.ClientResponse;
import sn.samabank.samabank_backend.entity.Client;

/**
 * Conversion Client → DTO (le mot de passe n'est jamais exposé).
 */
@Mapper
public interface ClientMapper {

    ClientResponse toResponse(Client client);
}
