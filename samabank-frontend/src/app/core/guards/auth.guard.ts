import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Role } from '../models/auth.model';
import { TokenService } from '../services/token.service';

/** Exige un utilisateur authentifié, sinon redirige vers /login. */
export const authGuard: CanActivateFn = () => {
  const tokens = inject(TokenService);
  const router = inject(Router);
  return tokens.estAuthentifie() ? true : router.createUrlTree(['/login']);
};

/** Exige au moins l'un des rôles indiqués. */
export const roleGuard = (...roles: Role[]): CanActivateFn => {
  return () => {
    const tokens = inject(TokenService);
    const router = inject(Router);
    if (!tokens.estAuthentifie()) {
      return router.createUrlTree(['/login']);
    }
    return roles.some((role) => tokens.aRole(role)) ? true : router.createUrlTree(['/login']);
  };
};
