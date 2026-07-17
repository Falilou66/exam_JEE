import { Component, inject } from '@angular/core';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  template: `
    <div class="fixed top-4 right-4 z-50 flex flex-col gap-2 w-80 max-w-[calc(100vw-2rem)]">
      @for (toast of toasts(); track toast.id) {
        <div
          class="animate-slide-in flex items-start gap-3 rounded-xl px-4 py-3 shadow-lg border text-sm"
          [class]="classes(toast.type)"
          role="status"
        >
          <span class="mt-0.5 font-bold">{{ icone(toast.type) }}</span>
          <p class="flex-1">{{ toast.message }}</p>
          <button
            type="button"
            (click)="service.fermer(toast.id)"
            class="text-current/60 hover:text-current"
            aria-label="Fermer"
          >✕</button>
        </div>
      }
    </div>
  `,
})
export class ToastContainer {
  protected readonly service = inject(ToastService);
  protected readonly toasts = this.service.toasts;

  protected classes(type: string): string {
    switch (type) {
      case 'success':
        return 'bg-white border-brand-200 text-brand-800';
      case 'error':
        return 'bg-white border-red-200 text-red-800';
      default:
        return 'bg-white border-slate-200 text-slate-800';
    }
  }

  protected icone(type: string): string {
    switch (type) {
      case 'success':
        return '✓';
      case 'error':
        return '✕';
      default:
        return 'ℹ';
    }
  }
}
