package sn.samabank.samabank_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.samabank.samabank_backend.dto.CompteResponse;
import sn.samabank.samabank_backend.entity.Compte;

/**
 * Conversion Compte (polymorphe) → DTO. Le type et le solde disponible sont
 * calculés par les méthodes polymorphes de l'entité.
 */
@Mapper
public interface CompteMapper {

    @Mapping(target = "type", expression = "java(compte.getType())")
    @Mapping(target = "soldeDisponible", expression = "java(compte.soldeDisponible())")
    @Mapping(target = "clientId", source = "client.id")
    CompteResponse toResponse(Compte compte);
}
