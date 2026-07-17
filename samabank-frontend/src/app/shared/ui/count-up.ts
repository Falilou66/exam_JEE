import { Component, computed, effect, input, signal } from '@angular/core';

/** Affiche un nombre en l'animant depuis 0 (easeOutCubic), formaté fr-FR. */
@Component({
  selector: 'app-count-up',
  template: `{{ affichage() }}`,
  styles: `:host { display: inline; }`,
})
export class CountUp {
  readonly value = input.required<number>();
  readonly fractionDigits = input(0);
  readonly duree = input(700);

  private readonly courant = signal(0);

  protected readonly affichage = computed(() =>
    new Intl.NumberFormat('fr-FR', {
      minimumFractionDigits: this.fractionDigits(),
      maximumFractionDigits: this.fractionDigits(),
    }).format(this.courant()),
  );

  constructor() {
    effect((onCleanup) => {
      const cible = this.value();
      const duree = this.duree();
      const debut = performance.now();
      let frame = 0;

      const tick = (maintenant: number) => {
        const progression = Math.min(1, (maintenant - debut) / duree);
        const eased = 1 - Math.pow(1 - progression, 3);
        this.courant.set(cible * eased);
        if (progression < 1) {
          frame = requestAnimationFrame(tick);
        }
      };

      frame = requestAnimationFrame(tick);
      onCleanup(() => cancelAnimationFrame(frame));
    });
  }
}
