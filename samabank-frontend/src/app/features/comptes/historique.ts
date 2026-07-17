import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TransactionResponse } from '../../core/models/banking.model';
import { Page } from '../../core/models/page.model';
import { CompteService } from '../../core/services/compte.service';

@Component({
  selector: 'app-historique',
  imports: [DecimalPipe, DatePipe, RouterLink],
  templateUrl: './historique.html',
})
export class Historique {
  private readonly route = inject(ActivatedRoute);
  private readonly compteService = inject(CompteService);

  protected readonly compteId = Number(this.route.snapshot.paramMap.get('id'));
  private readonly numero = this.route.snapshot.queryParamMap.get('numero') ?? '';

  protected readonly page = signal<Page<TransactionResponse> | null>(null);
  protected readonly indexPage = signal(0);
  protected readonly enCours = signal(true);

  constructor() {
    this.charger(0);
  }

  protected charger(index: number): void {
    this.enCours.set(true);
    this.indexPage.set(index);
    this.compteService.historique(this.compteId, index, 10).subscribe({
      next: (page) => {
        this.page.set(page);
        this.enCours.set(false);
      },
      error: () => this.enCours.set(false),
    });
  }

  protected precedent(): void {
    if (this.indexPage() > 0) {
      this.charger(this.indexPage() - 1);
    }
  }

  protected suivant(): void {
    const page = this.page();
    if (page && this.indexPage() < page.page.totalPages - 1) {
      this.charger(this.indexPage() + 1);
    }
  }

  /** Crédit (+) ou débit (-) du point de vue de ce compte. */
  protected estCredit(transaction: TransactionResponse): boolean {
    if (transaction.type === 'DEPOT') {
      return true;
    }
    if (transaction.type === 'RETRAIT') {
      return false;
    }
    // virement : crédit si ce compte est la destination
    return transaction.compteDestination === this.numero;
  }
}
