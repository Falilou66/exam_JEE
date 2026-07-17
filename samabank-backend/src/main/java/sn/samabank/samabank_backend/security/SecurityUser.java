package sn.samabank.samabank_backend.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sn.samabank.samabank_backend.entity.Utilisateur;

/**
 * Adapte une entité {@link Utilisateur} au contrat {@link UserDetails} de Spring
 * Security. Les autorités proviennent directement des libellés de rôles
 * ({@code ROLE_*}).
 */
public class SecurityUser implements UserDetails {

    private final transient Utilisateur utilisateur;

    public SecurityUser(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getLibelle()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.isActif();
    }

    public Set<String> getRoles() {
        return utilisateur.getRoles().stream()
                .map(role -> role.getLibelle())
                .collect(Collectors.toUnmodifiableSet());
    }
}
