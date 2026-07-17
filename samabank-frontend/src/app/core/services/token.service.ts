import { Injectable, computed, signal } from '@angular/core';
import { JwtPayload, Role } from '../models/auth.model';

const CLE_ACCESS = 'samabank.accessToken';
const CLE_REFRESH = 'samabank.refreshToken';

/**
 * Conserve les jetons (localStorage) et expose l'état d'authentification dérivé
 * du contenu de l'access token, sous forme de signals.
 */
@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly accessToken = signal<string | null>(localStorage.getItem(CLE_ACCESS));

  readonly payload = computed<JwtPayload | null>(() => this.decoder(this.accessToken()));
  readonly estAuthentifie = computed(() => {
    const p = this.payload();
    return !!p && p.exp * 1000 > Date.now();
  });
  readonly email = computed(() => this.payload()?.sub ?? null);
  readonly roles = computed<Role[]>(() => this.payload()?.roles ?? []);

  get access(): string | null {
    return this.accessToken();
  }

  get refresh(): string | null {
    return localStorage.getItem(CLE_REFRESH);
  }

  enregistrer(access: string, refresh: string): void {
    localStorage.setItem(CLE_ACCESS, access);
    localStorage.setItem(CLE_REFRESH, refresh);
    this.accessToken.set(access);
  }

  effacer(): void {
    localStorage.removeItem(CLE_ACCESS);
    localStorage.removeItem(CLE_REFRESH);
    this.accessToken.set(null);
  }

  aRole(role: Role): boolean {
    return this.roles().includes(role);
  }

  private decoder(token: string | null): JwtPayload | null {
    if (!token) {
      return null;
    }
    try {
      const charge = token.split('.')[1];
      const json = atob(charge.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json) as JwtPayload;
    } catch {
      return null;
    }
  }
}
