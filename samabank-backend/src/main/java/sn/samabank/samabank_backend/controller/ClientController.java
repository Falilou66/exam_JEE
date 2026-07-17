package sn.samabank.samabank_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.ClientResponse;
import sn.samabank.samabank_backend.dto.CreationClientRequest;
import sn.samabank.samabank_backend.service.ClientService;

/**
 * Gestion des clients — réservé au conseiller (SEQ-06, BF-07/BF-09).
 */
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONSEILLER')")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse creer(@Valid @RequestBody CreationClientRequest request) {
        return clientService.creer(request);
    }

    @GetMapping
    public PagedModel<ClientResponse> lister(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<ClientResponse> page = clientService.lister(pageable);
        return new PagedModel<>(page);
    }

    @GetMapping("/{id}")
    public ClientResponse consulter(@PathVariable Long id) {
        return clientService.consulter(id);
    }
}
