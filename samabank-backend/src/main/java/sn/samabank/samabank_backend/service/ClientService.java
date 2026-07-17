package sn.samabank.samabank_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.aspect.Auditable;
import sn.samabank.samabank_backend.dto.ClientResponse;
import sn.samabank.samabank_backend.dto.CreationClientRequest;
import sn.samabank.samabank_backend.entity.Client;
import sn.samabank.samabank_backend.entity.Role;
import sn.samabank.samabank_backend.exception.ClientIntrouvableException;
import sn.samabank.samabank_backend.exception.DonneeUniqueException;
import sn.samabank.samabank_backend.mapper.ClientMapper;
import sn.samabank.samabank_backend.repository.ClientRepository;
import sn.samabank.samabank_backend.repository.RoleRepository;
import sn.samabank.samabank_backend.repository.UtilisateurRepository;
import sn.samabank.samabank_backend.util.GenerateurNumero;

/**
 * Gestion du cycle de vie des clients (SEQ-06). Applique RG-8 (unicité
 * CNI/email) et rattache le rôle {@code ROLE_CLIENT} pour l'accès en ligne.
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    private static final String ROLE_CLIENT = "ROLE_CLIENT";

    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper clientMapper;
    private final GenerateurNumero generateurNumero;

    @Auditable(action = "CREATION_CLIENT", cibleType = "Client")
    @Transactional
    public ClientResponse creer(CreationClientRequest request) {
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new DonneeUniqueException("email", request.email());
        }
        if (clientRepository.existsByCni(request.cni())) {
            throw new DonneeUniqueException("cni", request.cni());
        }

        Client client = new Client();
        client.setNom(request.nom());
        client.setPrenom(request.prenom());
        client.setCni(request.cni());
        client.setEmail(request.email());
        client.setTelephone(request.telephone());
        client.setAdresse(request.adresse());
        client.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        client.setActif(true);
        client.setNumeroClient(
                generateurNumero.genererNumeroClient(clientRepository::existsByNumeroClient));
        client.ajouterRole(roleClient());

        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public ClientResponse consulter(Long id) {
        return clientMapper.toResponse(getClientOuErreur(id));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ClientResponse> lister(
            org.springframework.data.domain.Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::toResponse);
    }

    private Client getClientOuErreur(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientIntrouvableException(id));
    }

    private Role roleClient() {
        return roleRepository.findByLibelle(ROLE_CLIENT)
                .orElseThrow(() -> new IllegalStateException(
                        "Rôle " + ROLE_CLIENT + " absent : vérifier l'initialisation des données"));
    }
}
