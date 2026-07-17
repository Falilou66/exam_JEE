import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuditLogResponse } from '../../core/models/banking.model';
import { Page } from '../../core/models/page.model';
import { AdminService } from '../../core/services/admin.service';

@Component({
  selector: 'app-audit',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './audit.html',
})
export class Audit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(AdminService);

  protected readonly page = signal<Page<AuditLogResponse> | null>(null);
  protected readonly indexPage = signal(0);

  protected readonly filtres = this.fb.nonNullable.group({
    acteur: [''],
    action: [''],
    dateDebut: [''],
    dateFin: [''],
  });

  constructor() {
    this.charger(0);
  }

  protected charger(index: number): void {
    this.indexPage.set(index);
    const f = this.filtres.getRawValue();
    this.service
      .audit(
        {
          acteur: f.acteur || undefined,
          action: f.action || undefined,
          dateDebut: f.dateDebut ? `${f.dateDebut}T00:00:00Z` : undefined,
          dateFin: f.dateFin ? `${f.dateFin}T23:59:59Z` : undefined,
        },
        index,
        15,
      )
      .subscribe({ next: (p) => this.page.set(p) });
  }

  protected rechercher(): void {
    this.charger(0);
  }

  protected reinitialiser(): void {
    this.filtres.reset({ acteur: '', action: '', dateDebut: '', dateFin: '' });
    this.charger(0);
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
}
