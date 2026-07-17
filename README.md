<div align="center">

# 🏦 SamaBank

**Plateforme bancaire numérique sécurisée et auditable**

Application d'entreprise full-stack — API REST **Spring Boot** & front **Angular**.

`Spring Boot 4` · `Angular 22` · `MySQL 8` · `JWT` · `Tailwind CSS`

</div>

---

## 📖 Présentation

**SamaBank** permet aux **clients** de gérer leurs comptes en ligne (consultation,
virements, relevés PDF) et à la **banque** de superviser, tracer et sécuriser
toutes les opérations. Le principe directeur : un **noyau métier unique** exposé
en API REST, indépendant du canal d'accès.

Le projet couvre les **14 cas d'utilisation** du dossier de conception UML,
répartis sur trois acteurs aux responsabilités disjointes.

| Acteur | Rôle | Fonctions |
|---|---|---|
| 👤 **Client** | *utilise* | Consulter ses comptes, virements, historique, relevé PDF, profil |
| 🧑‍💼 **Conseiller** | *gère* | Créer des clients, ouvrir/bloquer des comptes, valider les opérations sensibles |
| 🛡️ **Administrateur** | *supervise* | Gérer les utilisateurs, consulter le journal d'audit |

---

## 🏗️ Architecture

```
┌─────────────────────────┐        REST / JSON        ┌──────────────────────────┐
│   samabank-frontend     │  ───── (HTTPS + JWT) ───▶  │    samabank-backend      │
│   Angular 22 + Tailwind │                            │   Spring Boot 4 (n-tiers)│
│   SPA, sidebar, signals │  ◀───────────────────────  │   Controller→Service→Repo│
└─────────────────────────┘                            └────────────┬─────────────┘
                                                                     │ JDBC
                                                            ┌────────▼─────────┐
                                                            │    MySQL 8       │
                                                            └──────────────────┘
```

**Backend** — architecture en couches stricte, sécurité JWT (access + refresh),
audit par AOP, gestion d'erreurs RFC 7807, séparation DTO/Entity (MapStruct).
**Frontend** — composants standalone, signals, guards par rôle, intercepteur JWT
avec rafraîchissement automatique, design system Tailwind « Fintech émeraude ».

---

## 🧰 Stack technique

| | Backend | Frontend |
|---|---|---|
| **Langage** | Java 21 | TypeScript |
| **Framework** | Spring Boot 4.1 | Angular 22 |
| **Sécurité** | Spring Security 7 + JWT (jjwt) | Guards + intercepteur JWT |
| **Données** | Spring Data JPA / Hibernate → MySQL 8 | — |
| **UI / autres** | MapStruct · AOP · OpenPDF · Lombok | Tailwind CSS v4 · signals · RxJS |
| **Build** | Maven (`./mvnw`) | Angular CLI (`ng`) |

---

## ✅ Prérequis

- **JDK 21+**
- **MySQL 8** en service local (port 3306)
- **Node.js 20+** et **npm**
- (Angular CLI recommandé : `npm i -g @angular/cli`)

---

## 🚀 Démarrage rapide

### 1. Base de données
Créer un utilisateur MySQL (ou utiliser un existant). Par défaut le backend se
connecte avec `spring_user` / `spring` et **crée la base `samabank`
automatiquement**.

### 2. Backend — API sur `http://localhost:8080`
```bash
cd samabank-backend
./mvnw spring-boot:run
```
> Identifiants MySQL personnalisés : `DB_USERNAME=root DB_PASSWORD=… ./mvnw spring-boot:run`

### 3. Frontend — SPA sur `http://localhost:4200`
```bash
cd samabank-frontend
npm install      # première fois
ng serve
```

Ouvrir **http://localhost:4200** 🎉

---

## 🔑 Comptes de démonstration

Créés automatiquement au premier démarrage du backend :

| Rôle | Email | Mot de passe |
|---|---|---|
| Administrateur | `admin@samabank.sn` | `Admin@123` |
| Conseiller | `conseiller@samabank.sn` | `Conseiller@123` |

> Les **clients** sont créés par un conseiller depuis l'interface (*Clients → Nouveau client*).

---

## ✨ Fonctionnalités clés

- 🔐 **Authentification JWT** — access token (15 min) + refresh (7 j) révocable au logout
- 🏦 **Comptes** courant / épargne, avec RIB généré automatiquement
- 💸 **Virements** atomiques (`@Transactional`) — au-delà de 5 000 000 FCFA : validation d'un conseiller
- 📄 **Relevés PDF** générés à la volée (OpenPDF)
- 📋 **Journal d'audit** append-only alimenté automatiquement par AOP
- 🎨 **Interface moderne** — sidebar responsive, tableaux de bord animés, toasts, modales

---

## 📁 Structure du dépôt

```
exam_JEE/
├── samabank-backend/     API REST Spring Boot   → voir son README
├── samabank-frontend/    SPA Angular            → voir son README
└── README.md             (ce fichier)
```

- 📘 [`samabank-backend/README.md`](samabank-backend/README.md) — détails API, endpoints, règles de gestion
- 📗 [`samabank-frontend/README.md`](samabank-frontend/README.md) — détails front, design system, structure

---

## 🛡️ Les 4 qualités défendues

| Qualité | Mise en œuvre |
|---|---|
| **Sécurisée** | Spring Security, JWT, BCrypt, `@PreAuthorize`, contrôle d'accès par rôle |
| **Auditable** | Aspect `@Auditable` → table `audit_log` append-only (lecture admin) |
| **Fiable** | Virement `@Transactional` — débit/crédit atomiques, rollback automatique |
| **Évolutive** | Couches étanches, séparation DTO/Entity, composants réutilisables |

---

<div align="center">
<sub>Projet Master 2 Systèmes d'Information — Développement d'Applications d'Entreprise (JEE)</sub>
</div>
