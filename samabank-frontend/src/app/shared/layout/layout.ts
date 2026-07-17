import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Icon, IconName } from '../ui/icon';

interface NavItem {
  label: string;
  route: string;
  icon: IconName;
}

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Icon],
  templateUrl: './layout.html',
})
export class Layout {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly email = this.auth.email;
  protected readonly roles = this.auth.roles;
  protected readonly menuOuvert = signal(false);

  protected readonly liens = computed<NavItem[]>(() => {
    const r = this.roles();
    const items: NavItem[] = [{ label: 'Accueil', route: '/accueil', icon: 'home' }];
    if (r.includes('ROLE_CLIENT')) {
      items.push(
        { label: 'Mes comptes', route: '/mes-comptes', icon: 'wallet' },
        { label: 'Virements', route: '/virements', icon: 'transfer' },
        { label: 'Profil', route: '/profil', icon: 'user' },
      );
    }
    if (r.includes('ROLE_CONSEILLER')) {
      items.push(
        { label: 'Clients', route: '/conseiller/clients', icon: 'users' },
        { label: 'Validations', route: '/conseiller/validations', icon: 'check' },
      );
    }
    if (r.includes('ROLE_ADMIN')) {
      items.push(
        { label: 'Utilisateurs', route: '/admin/utilisateurs', icon: 'shield' },
        { label: 'Audit', route: '/admin/audit', icon: 'audit' },
      );
    }
    return items;
  });

  protected readonly roleLabel = computed(() => {
    const r = this.roles();
    if (r.includes('ROLE_ADMIN')) return 'Administrateur';
    if (r.includes('ROLE_CONSEILLER')) return 'Conseiller';
    if (r.includes('ROLE_CLIENT')) return 'Client';
    return '';
  });

  protected readonly initiales = computed(() => (this.email() ?? '?').slice(0, 2).toUpperCase());

  protected estAccueil(route: string): boolean {
    return route === '/accueil';
  }

  protected fermerMenu(): void {
    this.menuOuvert.set(false);
  }

  protected basculerMenu(): void {
    this.menuOuvert.update((v) => !v);
  }

  protected deconnexion(): void {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']),
    });
  }
}
