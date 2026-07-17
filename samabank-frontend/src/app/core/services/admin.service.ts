import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuditLogResponse,
  CreationUtilisateurRequest,
  UtilisateurResponse,
} from '../models/banking.model';
import { Page } from '../models/page.model';

export interface FiltresAudit {
  acteur?: string;
  action?: string;
  dateDebut?: string;
  dateFin?: string;
}

/** Opérations d'administration : création d'utilisateurs et journal d'audit. */
@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  creerUtilisateur(requete: CreationUtilisateurRequest): Observable<UtilisateurResponse> {
    return this.http.post<UtilisateurResponse>(`${this.base}/admin/users`, requete);
  }

  audit(filtres: FiltresAudit, page = 0, size = 15): Observable<Page<AuditLogResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filtres.acteur) {
      params = params.set('acteur', filtres.acteur);
    }
    if (filtres.action) {
      params = params.set('action', filtres.action);
    }
    if (filtres.dateDebut) {
      params = params.set('dateDebut', filtres.dateDebut);
    }
    if (filtres.dateFin) {
      params = params.set('dateFin', filtres.dateFin);
    }
    return this.http.get<Page<AuditLogResponse>>(`${this.base}/audit`, { params });
  }
}
