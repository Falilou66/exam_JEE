package sn.samabank.samabank_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.dto.ClientResponse;
import sn.samabank.samabank_backend.dto.ProfilUpdateRequest;
import sn.samabank.samabank_backend.entity.Client;
import sn.samabank.samabank_backend.exception.ResourceNotFoundException;
import sn.samabank.samabank_backend.mapper.ClientMapper;
import sn.samabank.samabank_backend.repository.ClientRepository;

/**
 * Gestion du profil par le client lui-même (SEQ-12) : coordonnées et mot de
 * passe. Journalisé (MAJ_PROFIL).
 */
@Service
@RequiredArgsConstructor
public class ProfilService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper clientMapper;

    @Auditable(action = "MAJ_PROFIL", cibleType = "Client")
    @Transactional
    public ClientResponse mettreAJour(String email, ProfilUpdateRequest request) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable pour l'email : " + email));

        if (request.telephone() != null) {
            client.setTelephone(request.telephone());
        }
        if (request.adresse() != null) {
            client.setAdresse(request.adresse());
        }
        if (request.nouveauMotDePasse() != null && !request.nouveauMotDePasse().isBlank()) {
            client.setMotDePasse(passwordEncoder.encode(request.nouveauMotDePasse()));
        }
        // dirty checking : persisté à la fin de la transaction
        return clientMapper.toResponse(client);
    }
}
