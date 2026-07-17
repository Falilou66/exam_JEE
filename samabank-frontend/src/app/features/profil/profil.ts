import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProfilService } from '../../core/services/profil.service';
import { ToastService } from '../../core/services/toast.service';
import { Spinner } from '../../shared/ui/spinner';

@Component({
  selector: 'app-profil',
  imports: [ReactiveFormsModule, Spinner],
  templateUrl: './profil.html',
})
export class Profil {
  private readonly fb = inject(FormBuilder);
  private readonly profilService = inject(ProfilService);
  private readonly toast = inject(ToastService);

  protected readonly enCours = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    telephone: [''],
    adresse: [''],
    nouveauMotDePasse: ['', [Validators.minLength(6)]],
  });

  protected soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enCours.set(true);
    const valeurs = this.form.getRawValue();
    this.profilService
      .mettreAJour({
        telephone: valeurs.telephone || undefined,
        adresse: valeurs.adresse || undefined,
        nouveauMotDePasse: valeurs.nouveauMotDePasse || undefined,
      })
      .subscribe({
        next: () => {
          this.enCours.set(false);
          this.toast.success('Profil mis à jour.');
          this.form.controls.nouveauMotDePasse.reset('');
        },
        error: (err) => {
          this.enCours.set(false);
          this.toast.error(err?.error?.detail ?? 'La mise à jour a échoué.');
        },
      });
  }
}
