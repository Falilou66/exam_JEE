import { Component, inject } from '@angular/core';
import { ConfirmService } from '../../core/services/confirm.service';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    @if (demande(); as d) {
      <div class="fixed inset-0 z-[80] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm" (click)="service.repondre(false)" aria-hidden="true"></div>
        <div class="relative card p-6 w-full max-w-sm animate-pop-in" role="dialog" aria-modal="true">
          <h2 class="text-lg font-bold text-slate-800">{{ d.titre }}</h2>
          <p class="text-slate-600 mt-2 text-sm">{{ d.message }}</p>
          <div class="mt-6 flex justify-end gap-2">
            <button type="button" (click)="service.repondre(false)" class="btn btn-outline">Annuler</button>
            <button type="button" (click)="service.repondre(true)" [class]="d.danger ? 'btn btn-danger' : 'btn btn-primary'">
              {{ d.libelleConfirmer }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
})
export class ConfirmDialog {
  protected readonly service = inject(ConfirmService);
  protected readonly demande = this.service.demande;
}
