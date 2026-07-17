import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, Role } from '../models/auth.model';
import { TokenService } from './token.service';

/**
 * Authentification : login, renouvellement et déconnexion. Délègue le stockage
 * et l'état au {@link TokenService}.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokens = inject(TokenService);
  private readonly base = `${environment.apiUrl}/auth`;

  readonly estAuthentifie = this.tokens.estAuthentifie;
  readonly email = this.tokens.email;
  readonly roles = this.tokens.roles;

  login(identifiants: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/login`, identifiants)
      .pipe(tap((res) => this.tokens.enregistrer(res.accessToken, res.refreshToken)));
  }

  renouveler(): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/refresh`, { refreshToken: this.tokens.refresh })
      .pipe(tap((res) => this.tokens.enregistrer(res.accessToken, res.refreshToken)));
  }

  logout(): Observable<void> {
    const refreshToken = this.tokens.refresh;
    this.tokens.effacer();
    return this.http.post<void>(`${this.base}/logout`, { refreshToken });
  }

  aRole(role: Role): boolean {
    return this.tokens.aRole(role);
  }

  aUnDesRoles(...roles: Role[]): boolean {
    return roles.some((role) => this.tokens.aRole(role));
  }
}
