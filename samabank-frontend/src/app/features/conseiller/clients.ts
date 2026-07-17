import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ClientResponse } from '../../core/models/banking.model';
import { Page } from '../../core/models/page.model';
import { ConseillerService } from '../../core/services/conseiller.service';
import { ToastService } from '../../core/services/toast.service';
import { Icon } from '../../shared/ui/icon';
import { Spinner } from '../../shared/ui/spinner';

@Component({
  selector: 'app-clients',
  imports: [ReactiveFormsModule, RouterLink, Icon, Spinner],
  templateUrl: './clients.html',
})
export class Clients {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ConseillerService);
  private readonly toast = inject(ToastService);

  protected readonly page = signal<Page<ClientResponse> | null>(null);
  protected readonly indexPage = signal(0);
  protected readonly afficherFormulaire = signal(false);
  protected readonly enCours = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    cni: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(6)]],
    telephone: [''],
    adresse: [''],
  });

  constructor() {
    this.charger(0);
  }

  protected charger(index: number): void {
    this.indexPage.set(index);
    this.service.listerClients(index, 10).subscribe({ next: (p) => this.page.set(p) });
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

  protected creer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enCours.set(true);
    const v = this.form.getRawValue();
    this.service
      .creerClient({
        nom: v.nom,
        prenom: v.prenom,
        cni: v.cni,
        email: v.email,
        motDePasse: v.motDePasse,
        telephone: v.telephone || undefined,
        adresse: v.adresse || undefined,
      })
      .subscribe({
        next: (client) => {
          this.enCours.set(false);
          this.toast.success(`Client ${client.prenom} ${client.nom} créé (${client.numeroClient}).`);
          this.form.reset();
          this.afficherFormulaire.set(false);
          this.charger(0);
        },
        error: (err) => {
          this.enCours.set(false);
          this.toast.error(err?.error?.detail ?? 'Création impossible.');
        },
      });
  }
}
