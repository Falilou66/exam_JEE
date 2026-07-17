import { Component, input } from '@angular/core';

/** Indicateur de chargement circulaire, hérite de la couleur du texte. */
@Component({
  selector: 'app-spinner',
  template: `
    <svg
      class="animate-spin"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      [attr.width]="size()"
      [attr.height]="size()"
      aria-hidden="true"
    >
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
      <path class="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
    </svg>
  `,
  styles: `:host { display: inline-flex; line-height: 0; }`,
})
export class Spinner {
  readonly size = input(18);
}
