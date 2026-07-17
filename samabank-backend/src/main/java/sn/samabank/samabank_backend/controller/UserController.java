package sn.samabank.samabank_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.dto.CreationUtilisateurRequest;
import sn.samabank.samabank_backend.dto.UtilisateurResponse;
import sn.samabank.samabank_backend.service.UserService;

/**
 * Administration des utilisateurs internes (SEQ-10, BF-12) — réservé à l'admin.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody CreationUtilisateurRequest request) {
        return userService.creer(request);
    }
}
