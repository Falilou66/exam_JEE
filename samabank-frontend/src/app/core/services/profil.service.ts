import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClientResponse, ProfilUpdateRequest } from '../models/banking.model';

/** Mise à jour du profil du client connecté (coordonnées, mot de passe). */
@Injectable({ providedIn: 'root' })
export class ProfilService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  mettreAJour(requete: ProfilUpdateRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.base}/profil`, requete);
  }
}
