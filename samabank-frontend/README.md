# SamaBank — Frontend

Interface web (SPA) de la plateforme **SamaBank**, développée en **Angular 22**
avec **Tailwind CSS**. Consomme l'API REST du backend Spring Boot.

> ↩️ Vue d'ensemble du projet : [README principal](../README.md) ·
> API : [README backend](../samabank-backend/README.md)

---

## 🧰 Stack technique

| Domaine | Technologie |
|---|---|
| Framework | **Angular 22** (composants standalone, **signals**, control-flow `@if/@for`) |
| Langage | TypeScript (strict) |
| Style | **Tailwind CSS v4** — identité « Fintech émeraude » |
| HTTP / état | RxJS + `HttpClient`, intercepteur & guards **fonctionnels** |
| Build | Angular CLI (esbuild) |

---

## ✅ Prérequis

- **Node.js 20+** et **npm**
- Le **backend** démarré sur `http://localhost:8080` (voir son README)

---

## 🚀 Démarrage

```bash
npm install        # première fois
ng serve           # http://localhost:4200
```

L'URL de l'API est configurée dans `src/environments/environment.ts` :

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
};
```

### Build de production
```bash
ng build            # sortie dans dist/
```

---

## 🎨 Design system

Identité **Fintech émeraude** : accent émeraude/teal, sidebar bleu nuit, fond clair.

- **Tokens** (`src/styles.css`, `@theme`) : couleurs `brand-*` et `ink-*`, police *Plus Jakarta Sans*.
- **Classes réutilisables** (`@layer components`) : `.btn` (primary/outline/danger/success/ghost/sm), `.card`, `.input`, `.label`, `.badge`, `.alert`, `.table-clean`.
- **Composants UI** (`src/app/shared/ui/`) :
  - `Icon` — icônes Heroicons par nom
  - `Spinner` — indicateur de chargement
  - `CountUp` — compteurs animés
  - `ToastContainer` + `ToastService` — notifications
  - `ConfirmDialog` + `ConfirmService` — modales de confirmation
  - `RouteProgress` — barre de progression à la navigation
- **Animations** : `fade-in`, `slide-in`, `pop-in`, entrées en cascade (`.stagger`).

---

## 🗂️ Structure

```
src/app/
├── core/
│   ├── models/            interfaces (miroir des DTO backend)
│   ├── services/          auth · token (JWT signals) · compte · virement · profil · conseiller · admin · toast · confirm
│   ├── interceptors/      auth.interceptor (Bearer + refresh auto sur 401)
│   └── guards/            authGuard · roleGuard
├── shared/
│   ├── layout/            sidebar responsive (nav selon rôle)
│   └── ui/                composants réutilisables
├── features/
│   ├── auth/              login (split-screen)
│   ├── home/              tableau de bord par rôle (KPI animés)
│   ├── comptes/           mes-comptes · historique
│   ├── virements/         formulaire de virement
│   ├── profil/            mise à jour du profil
│   ├── conseiller/        clients · client-detail · validations
│   └── admin/             utilisateurs · audit
└── environments/          apiUrl
```

---

## 🔐 Authentification & sécurité

- Les jetons (access + refresh) sont stockés côté navigateur ; l'état d'auth est
  dérivé du JWT via des **signals** (`TokenService`).
- L'**intercepteur** ajoute le `Bearer` à chaque requête et **rafraîchit
  automatiquement** l'access token sur une réponse 401, avant de rejouer la requête.
- Les **guards** protègent les routes : `authGuard` (connecté) et
  `roleGuard('ROLE_…')` (par rôle). La navigation est en **lazy loading**.

---

## 🧭 Parcours par rôle

| Rôle | Pages |
|---|---|
| **Client** | Mes comptes · Historique · Relevé PDF · Virements · Profil |
| **Conseiller** | Clients · Détail client (ouvrir/bloquer comptes, **dépôt/retrait**) · Validations |
| **Admin** | Utilisateurs · Journal d'audit |

---

## 📝 Conventions

Le projet suit les bonnes pratiques Angular 22 (voir `.claude/CLAUDE.md`) :
composants **standalone**, **signals** + `computed()`, `inject()`, control-flow
natif, bindings `class`/`style` (pas de `ngClass`/`ngStyle`), formulaires réactifs.
