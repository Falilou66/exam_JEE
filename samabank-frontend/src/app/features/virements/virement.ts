import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompteResponse } from '../../core/models/banking.model';
import { CompteService } from '../../core/services/compte.service';
import { ToastService } from '../../core/services/toast.service';
import { VirementService } from '../../core/services/virement.service';
import { Spinner } from '../../shared/ui/spinner';

@Component({
  selector: 'app-virement',
  imports: [ReactiveFormsModule, DecimalPipe, Spinner],
  templateUrl: './virement.html',
})
export class Virement {
  private readonly fb = inject(FormBuilder);
  private readonly compteService = inject(CompteService);
  private readonly virementService = inject(VirementService);
  private readonly toast = inject(ToastService);

  protected readonly comptes = signal<CompteResponse[]>([]);
  protected readonly enCours = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    compteSource: ['', Validators.required],
    compteDestination: ['', Validators.required],
    montant: [null as number | null, [Validators.required, Validators.min(1)]],
    motif: [''],
  });

  constructor() {
    this.compteService.mesComptes().subscribe({
      next: (comptes) => this.comptes.set(comptes.filter((c) => c.statut === 'ACTIF')),
    });
  }

  protected soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enCours.set(true);
    const valeurs = this.form.getRawValue();
    this.virementService
      .effectuer({
        compteSource: valeurs.compteSource,
        compteDestination: valeurs.compteDestination,
        montant: valeurs.montant ?? 0,
        motif: valeurs.motif || undefined,
      })
      .subscribe({
        next: (transaction) => {
          this.enCours.set(false);
          if (transaction.statut === 'EN_ATTENTE_VALIDATION') {
            this.toast.info('Virement en attente de validation (montant supérieur au seuil).');
          } else {
            this.toast.success('Virement effectué avec succès.');
          }
          this.form.reset();
        },
        error: (err) => {
          this.enCours.set(false);
          this.toast.error(err?.error?.detail ?? 'Le virement a échoué.');
        },
      });
  }
}
