import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { DecisionValidation, TransactionResponse } from '../../core/models/banking.model';
import { Page } from '../../core/models/page.model';
import { ConseillerService } from '../../core/services/conseiller.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-validations',
  imports: [DecimalPipe, DatePipe],
  templateUrl: './validations.html',
})
export class Validations {
  private readonly service = inject(ConseillerService);
  private readonly toast = inject(ToastService);

  protected readonly page = signal<Page<TransactionResponse> | null>(null);
  protected readonly indexPage = signal(0);
  protected readonly traitement = signal<number | null>(null);

  constructor() {
    this.charger(0);
  }

  protected charger(index: number): void {
    this.indexPage.set(index);
    this.service.operationsEnAttente(index, 10).subscribe({ next: (p) => this.page.set(p) });
  }

  protected precedent(): void {
    if (this.indexPage() > 0) {
      this.charger(this.indexPage() - 1);
    }
  }

  protected suivant(): void {
    const p = this.page();
    if (p && this.indexPage() < p.page.totalPages - 1) {
      this.charger(this.indexPage() + 1);
    }
  }

  protected traiter(transaction: TransactionResponse, decision: DecisionValidation): void {
    this.traitement.set(transaction.id);
    this.service.valider(transaction.id, decision).subscribe({
      next: (resultat) => {
        this.traitement.set(null);
        this.toast.success(`Opération ${resultat.reference} : ${resultat.statut}.`);
        this.charger(this.indexPage());
      },
      error: (err) => {
        this.traitement.set(null);
        this.toast.error(err?.error?.detail ?? 'Traitement impossible.');
      },
    });
  }
}
