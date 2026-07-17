# SamaBank — Backend

Plateforme bancaire numérique **sécurisée et auditable**. API REST développée en
**Spring Boot** selon une architecture n-tiers, consommée par un front Angular.

> Projet Master 2 SI (UADB) — Développement d'Applications d'Entreprise (JEE).
> Le backend implémente les 14 cas d'usage du dossier de conception UML.

---

## 🧱 Stack technique

| Domaine | Technologie |
|---|---|
| Langage / runtime | **Java 21** |
| Framework | **Spring Boot 4.1** (Spring Framework 7, Jakarta EE 11) |
| Sécurité | Spring Security 7 + **JWT** (jjwt) |
| Persistance | Spring Data JPA / Hibernate |
| Base de données | **MySQL 8** |
| Mapping DTO ↔ Entité | **MapStruct** |
| Audit | Spring AOP (AspectJ) |
| Génération PDF | **OpenPDF** |
| Build | Maven (wrapper `./mvnw`) |
| Boilerplate | Lombok |

---

## 🏛️ Architecture

Architecture **en couches strictes** (package-by-layer), le métier étant
indépendant du canal d'accès :

```
Controller  (@RestController)   → exposition HTTP, validation des DTO
    ↓
Service     (@Service @Transactional) → logique métier, règles de gestion
    ↓
Repository  (Spring Data JPA)    → accès aux données
    ↓
Entity      (JPA / Hibernate)    → mapping, héritage JOINED
    ↓
MySQL
```

Éléments transverses : filtre **JWT**, **AuditAspect** (AOP), gestion centralisée
des exceptions (`@RestControllerAdvice` → **RFC 7807 / ProblemDetail**), séparation
stricte **DTO / Entity** (MapStruct).

```
sn.samabank.samabank_backend
├── config/       SecurityConfig · JpaAuditingConfig · SamaBankProperties · DataInitializer
├── controller/   Auth · Client · Compte · Virement · Transaction · Validation · Operation · Audit · Releve · Profil · User
├── service/      logique métier par domaine
├── repository/   interfaces Spring Data JPA
├── entity/       Utilisateur/Compte/Transaction (héritage JOINED) · AuditLog · TokenRevoque
├── dto/          records de requête / réponse
├── mapper/       mappers MapStruct
├── enums/        StatutCompte · StatutTransaction · TypeCompte · Canal · …
├── security/     JwtService · JwtAuthenticationFilter · SecurityUser · handlers 401/403
├── aspect/       @Auditable · AuditAspect
├── exception/    BusinessException · GlobalExceptionHandler
└── util/         GenerateurNumero (RIB, n° client, références)
```

---

## ✅ Prérequis

- **JDK 21+**
- **MySQL 8** en service local (port 3306)
- Un utilisateur MySQL disposant du droit de créer une base

> Maven n'a pas besoin d'être installé : utilisez le wrapper `./mvnw`.

---

## ⚙️ Configuration

La configuration est dans `src/main/resources/application.properties`. Les valeurs
sensibles sont **surchargeables par variables d'environnement** :

| Propriété | Variable d'env | Défaut |
|---|---|---|
| Utilisateur MySQL | `DB_USERNAME` | `spring_user` |
| Mot de passe MySQL | `DB_PASSWORD` | `spring` |
| Secret JWT | `JWT_SECRET` | *(secret de dev)* |

La base `samabank` est **créée automatiquement** au démarrage
(`createDatabaseIfNotExist=true`). Le schéma est géré par Hibernate
(`ddl-auto=update`).

Paramètres métier notables :

```properties
samabank.security.jwt.access-expiration-ms=900000     # access token : 15 min
samabank.security.jwt.refresh-expiration-ms=604800000 # refresh token : 7 jours
samabank.banque.seuil-validation-virement=5000000     # RG-4 : seuil (FCFA)
```

---

## 🚀 Démarrage

```bash
# depuis le dossier samabank-backend
./mvnw spring-boot:run
```

Avec des identifiants MySQL personnalisés :

```bash
DB_USERNAME=root DB_PASSWORD=monmdp ./mvnw spring-boot:run
```

L'API écoute sur **http://localhost:8080**. Au premier démarrage, les rôles et
deux comptes de test sont créés automatiquement.

### Comptes de démonstration

| Rôle | Email | Mot de passe |
|---|---|---|
| Administrateur | `admin@samabank.sn` | `Admin@123` |
| Conseiller | `conseiller@samabank.sn` | `Conseiller@123` |

*(Les clients sont créés par un conseiller via l'API.)*

---

## 🔐 Authentification

Auth **stateless** par **access token** (court) + **refresh token** (long,
révocable au logout via une denylist).

```bash
# Connexion
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@samabank.sn","motDePasse":"Admin@123"}'
```

```json
{ "accessToken": "eyJ...", "refreshToken": "eyJ...", "tokenType": "Bearer", "expiresIn": 900 }
```

Les requêtes protégées portent l'en-tête `Authorization: Bearer <accessToken>`.
Le **refresh token ne peut pas** appeler l'API (claim `type` vérifié).

| Endpoint | Description |
|---|---|
| `POST /api/v1/auth/login` | Connexion → paire de jetons |
| `POST /api/v1/auth/refresh` | Renouveler l'access token |
| `POST /api/v1/auth/logout` | Révoquer le refresh token (204) |

---

## 📚 API REST (`/api/v1`)

### Client (`ROLE_CLIENT`)
| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/comptes` | Ses comptes et soldes |
| `POST` | `/virements` | Effectuer un virement (201, ou 202 si > seuil) |
| `GET` | `/comptes/{id}/transactions` | Historique paginé |
| `GET` | `/comptes/{id}/releve?mois=AAAA-MM` | Relevé **PDF** |
| `PUT` | `/profil` | Mettre à jour ses coordonnées / mot de passe |

### Conseiller (`ROLE_CONSEILLER`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/clients` | Créer un client |
| `GET` | `/clients/{id}` | Dossier d'un client |
| `POST` | `/clients/{id}/comptes` | Ouvrir un compte (courant / épargne) |
| `GET` | `/clients/{id}/comptes` | Comptes d'un client |
| `PATCH` | `/comptes/{id}/statut` | Bloquer / débloquer / clôturer |
| `POST` | `/comptes/{id}/depot` | Dépôt |
| `POST` | `/comptes/{id}/retrait` | Retrait |
| `POST` | `/transactions/{id}/valider` | Approuver / rejeter une opération sensible |

### Administrateur (`ROLE_ADMIN`)
| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/users` | Créer un conseiller / administrateur |
| `GET` | `/audit` | Journal d'audit (filtres `acteur`, `action`, `dateDebut`, `dateFin`) |

### Exemple — virement

```bash
curl -X POST http://localhost:8080/api/v1/virements \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"compteSource":"SN...","compteDestination":"SN...","montant":30000,"motif":"Loyer"}'
```

---

## 📏 Règles de gestion

| Réf | Règle |
|---|---|
| RG-1 | Un client possède plusieurs comptes ; un compte a un seul client |
| RG-2 | Virement possible seulement si le compte source est `ACTIF` |
| RG-3 | Virement possible seulement si le solde disponible est suffisant (solde + découvert pour un courant) |
| RG-4 | Virement > **5 000 000 FCFA** → `EN_ATTENTE_VALIDATION` (approbation conseiller) |
| RG-5 | Un compte ne se clôture que si son solde est nul |
| RG-6 | Un compte non actif n'autorise ni débit ni crédit |
| RG-7 | Le numéro de compte (RIB) est généré automatiquement et immuable |
| RG-8 | CNI et email d'un client sont uniques |

---

## 🧭 Format des erreurs (RFC 7807)

Toutes les erreurs sont renvoyées en `application/problem+json` :

```json
{
  "status": 422,
  "title": "Unprocessable Content",
  "detail": "Solde disponible insuffisant sur le compte SN...",
  "code": "SOLDE_INSUFFISANT",
  "timestamp": "2026-07-17T18:00:00Z"
}
```

---

## 🛡️ Les 4 qualités

| Qualité | Mise en œuvre |
|---|---|
| **Sécurisée** | Spring Security, JWT, BCrypt, `@PreAuthorize`, contrôle d'accès par rôle |
| **Auditable** | Aspect `@Auditable` → table `audit_log` **append-only** (lecture ADMIN) |
| **Fiable** | Virement `@Transactional` — débit/crédit atomiques, rollback automatique |
| **Évolutive** | Couches étanches, séparation DTO/Entity, modules mockés derrière interfaces |

---

## 🧪 Build & tests

```bash
./mvnw clean package     # build (jar exécutable dans target/)
./mvnw test              # tests
```

---

## 🗺️ Endpoints utilitaires

| Endpoint | Accès |
|---|---|
| `GET /actuator/health` | Public — état de l'application |

---

*CORS ouvert à `http://localhost:4200` pour le front Angular.*
