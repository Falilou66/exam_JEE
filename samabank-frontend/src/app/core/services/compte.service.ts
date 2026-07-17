import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CompteResponse, TransactionResponse } from '../models/banking.model';
import { Page } from '../models/page.model';

/**
 * Accès aux comptes du client connecté : liste, historique paginé, relevé PDF.
 */
@Injectable({ providedIn: 'root' })
export class CompteService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  mesComptes(): Observable<CompteResponse[]> {
    return this.http.get<CompteResponse[]>(`${this.base}/comptes`);
  }

  historique(compteId: number, page = 0, size = 10): Observable<Page<TransactionResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<TransactionResponse>>(
      `${this.base}/comptes/${compteId}/transactions`,
      { params },
    );
  }

  releve(compteId: number, mois: string): Observable<Blob> {
    return this.http.get(`${this.base}/comptes/${compteId}/releve`, {
      params: new HttpParams().set('mois', mois),
      responseType: 'blob',
    });
  }
}
