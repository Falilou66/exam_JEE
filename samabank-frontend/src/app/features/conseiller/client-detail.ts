import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  ClientResponse,
  CompteResponse,
  OuvertureCompteRequest,
  StatutCompte,
  TypeCompte,
} from '../../core/models/banking.model';
import { ConfirmService } from '../../core/services/confirm.service';
import { ConseillerService } from '../../core/services/conseiller.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-client-detail',
  imports: [ReactiveFormsModule, RouterLink, DecimalPipe],
  templateUrl: './client-detail.html',
})
export class ClientDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(ConseillerService);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);
  private readonly fb = inject(FormBuilder);

  protected readonly clientId = Number(this.route.snapshot.paramMap.get('id'));
  protected readonly client = signal<ClientResponse | null>(null);
  protected readonly comptes = signal<CompteResponse[]>([]);

  protected readonly form = this.fb.nonNullable.group({
    type: ['COURANT' as TypeCompte],
    decouvertAutorise: [0],
    tauxInteret: [0],
  });

  // Opération de caisse (dépôt / retrait) en cours de saisie
  protected readonly operation = signal<{ compteId: number; type: 'DEPOT' | 'RETRAIT' } | null>(null);
  protected readonly enCoursOp = signal(false);
  protected readonly montantOp = new FormControl<number | null>(null, {
    validators: [Validators.required, Validators.min(1)],
  });

  constructor() {
    this.chargerClient();
    this.chargerComptes();
  }

  private chargerClient(): void {
    this.service.consulterClient(this.clientId).subscribe({
      next: (c) => this.client.set(c),
      error: () => this.toast.error('Client introuvable.'),
    });
  }

  private chargerComptes(): void {
    this.service.comptesClient(this.clientId).subscribe({ next: (cs) => this.comptes.set(cs) });
  }

  protected ouvrir(): void {
    const v = this.form.getRawValue();
    const requete: OuvertureCompteRequest = {
      type: v.type,
      decouvertAutorise: v.type === 'COURANT' ? v.decouvertAutorise : undefined,
      tauxInteret: v.type === 'EPARGNE' ? v.tauxInteret : undefined,
    };
    this.service.ouvrirCompte(this.clientId, requete).subscribe({
      next: () => {
        this.toast.success('Compte ouvert.');
        this.chargerComptes();
      },
      error: (err) => this.toast.error(err?.error?.detail ?? "Échec de l'ouverture."),
    });
  }

  protected async changer(compte: CompteResponse, statut: StatutCompte): Promise<void> {
    const action = statut === 'BLOQUE' ? 'bloquer' : statut === 'ACTIF' ? 'débloquer' : 'clôturer';
    const confirme = await this.confirm.demander(
      `Voulez-vous ${action} le compte ${compte.numeroCompte} ?`,
      {
        titre: 'Confirmer l\'action',
        libelleConfirmer: action.charAt(0).toUpperCase() + action.slice(1),
        danger: statut !== 'ACTIF',
      },
    );
    if (!confirme) {
      return;
    }
    this.service.changerStatut(compte.id, statut).subscribe({
      next: () => {
        this.toast.success('Statut mis à jour.');
        this.chargerComptes();
      },
      error: (err) => this.toast.error(err?.error?.detail ?? 'Action impossible.'),
    });
  }

  protected ouvrirOperation(compte: CompteResponse, type: 'DEPOT' | 'RETRAIT'): void {
    this.operation.set({ compteId: compte.id, type });
    this.montantOp.reset(null);
  }

  protected annulerOperation(): void {
    this.operation.set(null);
  }

  protected soumettreOperation(compte: CompteResponse): void {
    if (this.montantOp.invalid) {
      this.montantOp.markAsTouched();
      return;
    }
    const op = this.operation();
    if (!op) {
      return;
    }
    this.enCoursOp.set(true);
    const requete = { montant: this.montantOp.value ?? 0 };
    const appel =
      op.type === 'DEPOT'
        ? this.service.deposer(compte.id, requete)
        : this.service.retirer(compte.id, requete);

    appel.subscribe({
      next: () => {
        this.enCoursOp.set(false);
        this.operation.set(null);
        this.toast.success(op.type === 'DEPOT' ? 'Dépôt effectué.' : 'Retrait effectué.');
        this.chargerComptes();
      },
      error: (err) => {
        this.enCoursOp.set(false);
        this.toast.error(err?.error?.detail ?? "L'opération a échoué.");
      },
    });
  }

  protected badgeStatut(statut: StatutCompte): string {
    switch (statut) {
      case 'ACTIF':
        return 'badge badge-green';
      case 'BLOQUE':
        return 'badge badge-red';
      case 'CLOTURE':
        return 'badge badge-slate';
      default:
        return 'badge badge-amber';
    }
  }
}
