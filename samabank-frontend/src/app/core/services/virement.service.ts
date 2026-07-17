import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TransactionResponse, VirementRequest } from '../models/banking.model';

/**
 * Exécution des virements (client). La réponse porte le statut : VALIDEE
 * (exécuté) ou EN_ATTENTE_VALIDATION (montant supérieur au seuil).
 */
@Injectable({ providedIn: 'root' })
export class VirementService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  effectuer(requete: VirementRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.base}/virements`, requete);
  }
}
