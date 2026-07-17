import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info';

export interface Toast {
  id: number;
  type: ToastType;
  message: string;
}

/** Notifications éphémères (auto-disparition après ~4 s). */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private compteur = 0;
  readonly toasts = signal<Toast[]>([]);

  success(message: string): void {
    this.afficher('success', message);
  }
  error(message: string): void {
    this.afficher('error', message);
  }
  info(message: string): void {
    this.afficher('info', message);
  }

  fermer(id: number): void {
    this.toasts.update((liste) => liste.filter((t) => t.id !== id));
  }

  private afficher(type: ToastType, message: string): void {
    const id = ++this.compteur;
    this.toasts.update((liste) => [...liste, { id, type, message }]);
    setTimeout(() => this.fermer(id), 4000);
  }
}
