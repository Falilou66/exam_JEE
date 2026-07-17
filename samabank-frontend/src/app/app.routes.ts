import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login').then((m) => m.Login),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./shared/layout/layout').then((m) => m.Layout),
    children: [
      {
        path: 'accueil',
        loadComponent: () => import('./features/home/home').then((m) => m.Home),
      },
      {
        path: 'mes-comptes',
        canActivate: [roleGuard('ROLE_CLIENT')],
        loadComponent: () => import('./features/comptes/mes-comptes').then((m) => m.MesComptes),
      },
      {
        path: 'comptes/:id/transactions',
        loadComponent: () => import('./features/comptes/historique').then((m) => m.Historique),
      },
      {
        path: 'virements',
        canActivate: [roleGuard('ROLE_CLIENT')],
        loadComponent: () => import('./features/virements/virement').then((m) => m.Virement),
      },
      {
        path: 'profil',
        canActivate: [roleGuard('ROLE_CLIENT')],
        loadComponent: () => import('./features/profil/profil').then((m) => m.Profil),
      },
      {
        path: 'conseiller/clients',
        canActivate: [roleGuard('ROLE_CONSEILLER')],
        loadComponent: () => import('./features/conseiller/clients').then((m) => m.Clients),
      },
      {
        path: 'conseiller/clients/:id',
        canActivate: [roleGuard('ROLE_CONSEILLER')],
        loadComponent: () =>
          import('./features/conseiller/client-detail').then((m) => m.ClientDetail),
      },
      {
        path: 'conseiller/validations',
        canActivate: [roleGuard('ROLE_CONSEILLER')],
        loadComponent: () => import('./features/conseiller/validations').then((m) => m.Validations),
      },
      {
        path: 'admin/utilisateurs',
        canActivate: [roleGuard('ROLE_ADMIN')],
        loadComponent: () => import('./features/admin/utilisateurs').then((m) => m.Utilisateurs),
      },
      {
        path: 'admin/audit',
        canActivate: [roleGuard('ROLE_ADMIN')],
        loadComponent: () => import('./features/admin/audit').then((m) => m.Audit),
      },
      { path: '', pathMatch: 'full', redirectTo: 'accueil' },
    ],
  },
  { path: '**', redirectTo: '' },
];
