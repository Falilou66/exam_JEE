import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { TokenService } from '../services/token.service';

/**
 * Ajoute le Bearer access token à chaque requête protégée. Sur une réponse 401,
 * tente un renouvellement (une seule fois) via le refresh token, puis rejoue la
 * requête ; en cas d'échec, purge la session et redirige vers /login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenService);
  const auth = inject(AuthService);
  const router = inject(Router);

  const estEndpointAuth = req.url.includes('/auth/');
  const access = tokens.access;
  const requete =
    access && !estEndpointAuth
      ? req.clone({ setHeaders: { Authorization: `Bearer ${access}` } })
      : req;

  return next(requete).pipe(
    catchError((erreur: HttpErrorResponse) => {
      const peutRenouveler = erreur.status === 401 && !estEndpointAuth && !!tokens.refresh;
      if (!peutRenouveler) {
        return throwError(() => erreur);
      }
      return auth.renouveler().pipe(
        switchMap((res) =>
          next(req.clone({ setHeaders: { Authorization: `Bearer ${res.accessToken}` } })),
        ),
        catchError((echec) => {
          tokens.effacer();
          router.navigate(['/login']);
          return throwError(() => echec);
        }),
      );
    }),
  );
};
