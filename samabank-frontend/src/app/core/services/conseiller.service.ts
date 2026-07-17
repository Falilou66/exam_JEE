import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ClientResponse,
  CompteResponse,
  CreationClientRequest,
  DecisionValidation,
  OuvertureCompteRequest,
  StatutCompte,
  TransactionResponse,
} from '../models/banking.model';
import { Page } from '../models/page.model';

/** Ensemble des opérations du conseiller (clients, comptes, validations). */
@Injectable({ providedIn: 'root' })
export class ConseillerService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  listerClients(page = 0, size = 20): Observable<Page<ClientResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<ClientResponse>>(`${this.base}/clients`, { params });
  }

  creerClient(requete: CreationClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(`${this.base}/clients`, requete);
  }

  consulterClient(id: number): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.base}/clients/${id}`);
  }

  comptesClient(id: number): Observable<CompteResponse[]> {
    return this.http.get<CompteResponse[]>(`${this.base}/clients/${id}/comptes`);
  }

  ouvrirCompte(clientId: number, requete: OuvertureCompteRequest): Observable<CompteResponse> {
    return this.http.post<CompteResponse>(`${this.base}/clients/${clientId}/comptes`, requete);
  }

  changerStatut(compteId: number, statut: StatutCompte): Observable<CompteResponse> {
    return this.http.patch<CompteResponse>(`${this.base}/comptes/${compteId}/statut`, { statut });
  }

  operationsEnAttente(page = 0, size = 20): Observable<Page<TransactionResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<TransactionResponse>>(`${this.base}/transactions/en-attente`, { params });
  }

  valider(transactionId: number, decision: DecisionValidation): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(
      `${this.base}/transactions/${transactionId}/valider`,
      { decision },
    );
  }
}
