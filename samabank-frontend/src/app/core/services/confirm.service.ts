import { Injectable, signal } from '@angular/core';

export interface DemandeConfirmation {
  titre: string;
  message: string;
  libelleConfirmer: string;
  danger: boolean;
  resolve: (valeur: boolean) => void;
}

/** Ouvre une modale de confirmation et résout une promesse selon le choix. */
@Injectable({ providedIn: 'root' })
export class ConfirmService {
  readonly demande = signal<DemandeConfirmation | null>(null);

  demander(
    message: string,
    options: { titre?: string; libelleConfirmer?: string; danger?: boolean } = {},
  ): Promise<boolean> {
    return new Promise((resolve) => {
      this.demande.set({
        message,
        titre: options.titre ?? 'Confirmation',
        libelleConfirmer: options.libelleConfirmer ?? 'Confirmer',
        danger: options.danger ?? false,
        resolve,
      });
    });
  }

  repondre(valeur: boolean): void {
    const demande = this.demande();
    if (demande) {
      demande.resolve(valeur);
      this.demande.set(null);
    }
  }
}
