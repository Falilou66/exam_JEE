import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
} from '@angular/router';

/** Fine barre de chargement animée en haut de l'écran pendant la navigation. */
@Component({
  selector: 'app-route-progress',
  template: `
    @if (actif()) {
      <div class="fixed top-0 left-0 right-0 h-0.5 z-[70] bg-brand-100 overflow-hidden">
        <div class="h-full w-2/5 bg-brand-500 animate-progress"></div>
      </div>
    }
  `,
})
export class RouteProgress {
  private readonly router = inject(Router);
  protected readonly actif = signal(false);

  constructor() {
    this.router.events.pipe(takeUntilDestroyed()).subscribe((evenement) => {
      if (evenement instanceof NavigationStart) {
        this.actif.set(true);
      } else if (
        evenement instanceof NavigationEnd ||
        evenement instanceof NavigationCancel ||
        evenement instanceof NavigationError
      ) {
        this.actif.set(false);
      }
    });
  }
}
