import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TypeUtilisateur } from '../../core/models/banking.model';
import { AdminService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { Spinner } from '../../shared/ui/spinner';

@Component({
  selector: 'app-utilisateurs',
  imports: [ReactiveFormsModule, Spinner],
  templateUrl: './utilisateurs.html',
})
export class Utilisateurs {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(AdminService);
  private readonly toast = inject(ToastService);

  protected readonly enCours = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(6)]],
    type: ['CONSEILLER' as TypeUtilisateur],
    matricule: [''],
    agence: [''],
    niveauAcces: [1],
  });

  protected soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enCours.set(true);
    const v = this.form.getRawValue();
    this.service
      .creerUtilisateur({
        email: v.email,
        motDePasse: v.motDePasse,
        type: v.type,
        matricule: v.type === 'CONSEILLER' ? v.matricule || undefined : undefined,
        agence: v.type === 'CONSEILLER' ? v.agence || undefined : undefined,
        niveauAcces: v.type === 'ADMIN' ? v.niveauAcces : undefined,
      })
      .subscribe({
        next: (u) => {
          this.enCours.set(false);
          this.toast.success(`Utilisateur ${u.email} créé (${u.type}).`);
          this.form.reset({ type: 'CONSEILLER', niveauAcces: 1 });
        },
        error: (err) => {
          this.enCours.set(false);
          this.toast.error(err?.error?.detail ?? 'Création impossible.');
        },
      });
  }
}
