import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CompteResponse, StatutCompte } from '../../core/models/banking.model';
import { CompteService } from '../../core/services/compte.service';
import { ToastService } from '../../core/services/toast.service';
import { Icon } from '../../shared/ui/icon';

@Component({
  selector: 'app-mes-comptes',
  imports: [RouterLink, DecimalPipe, Icon],
  templateUrl: './mes-comptes.html',
})
export class MesComptes {
  private readonly compteService = inject(CompteService);
  private readonly toast = inject(ToastService);

  protected readonly comptes = signal<CompteResponse[]>([]);
  protected readonly enCours = signal(true);
  protected readonly telechargement = signal<number | null>(null);

  constructor() {
    this.compteService.mesComptes().subscribe({
      next: (comptes) => {
        this.comptes.set(comptes);
        this.enCours.set(false);
      },
      error: () => {
        this.enCours.set(false);
        this.toast.error('Impossible de charger vos comptes.');
      },
    });
  }

  protected telechargerReleve(compte: CompteResponse): void {
    const mois = this.moisCourant();
    this.telechargement.set(compte.id);
    this.compteService.releve(compte.id, mois).subscribe({
      next: (blob) => {
        this.enregistrer(blob, `releve-${compte.numeroCompte}-${mois}.pdf`);
        this.telechargement.set(null);
        this.toast.success('Relevé téléchargé.');
      },
      error: () => {
        this.telechargement.set(null);
        this.toast.error('Échec du téléchargement du relevé.');
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

  private moisCourant(): string {
    const maintenant = new Date();
    const mois = String(maintenant.getMonth() + 1).padStart(2, '0');
    return `${maintenant.getFullYear()}-${mois}`;
  }

  private enregistrer(blob: Blob, nom: string): void {
    const url = URL.createObjectURL(blob);
    const lien = document.createElement('a');
    lien.href = url;
    lien.download = nom;
    lien.click();
    URL.revokeObjectURL(url);
  }
}
