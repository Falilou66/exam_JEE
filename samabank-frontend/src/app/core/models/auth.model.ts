export type Role = 'ROLE_CLIENT' | 'ROLE_CONSEILLER' | 'ROLE_ADMIN';

export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

/** Contenu utile du JWT (claims). */
export interface JwtPayload {
  sub: string;
  roles: Role[];
  exp: number;
  iat: number;
  iss?: string;
  type?: string;
}
